package dev.oreui.testmod;

import com.mojang.blaze3d.platform.InputConstants;
import dev.oreui.adapter262.Fabric26Pipelines;
import dev.oreui.api.OreUI;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class OreUIGalleryClient implements ClientModInitializer {
    private static final Logger LOG = LoggerFactory.getLogger("oreui-testmod");
    private static final boolean SMOKE = Boolean.parseBoolean(System.getProperty("orui.smoke", "false"));
    private static final String SHOT_NAME = System.getProperty("orui.shot", "oreui-gallery-smoke");
    private static final boolean WORLD = Boolean.parseBoolean(System.getProperty("orui.world", "false"));
    private static final boolean V2 = Boolean.parseBoolean(System.getProperty("orui.v2", "false"));
    private static final boolean SHOWCASE = Boolean.parseBoolean(System.getProperty("orui.showcase", "false"));
    private static final boolean RESIZE_TEST = Boolean.parseBoolean(System.getProperty("orui.resizeTest", "false"));

    private int ticks;
    private int stopAt = -1;
    private int captureAt = -1;
    private boolean titleSeen;
    private boolean captured;
    private boolean galleryOpened;
    private boolean showcaseKeyDown;
    private int resizeStep;

    @Override
    public void onInitializeClient() {
        Fabric26Pipelines.initialize();
        LOG.info("[oreui] testmod initialized ({}:{})", OreUI.MOD_ID, OreUI.VERSION);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ticks++;
            if (!titleSeen && client.gui.screen() instanceof TitleScreen && client.gui.overlay() == null) {
                titleSeen = true;
                if (WORLD) {
                    openSmokeWorld(client, client.gui.screen());
                    LOG.info("[oreui] smoke world requested");
                } else if (SMOKE) {
                    if (SHOWCASE) {
                        client.gui.setScreen(new OreComponentShowcaseScreen());
                        LOG.info("[oreui] showcase screen opened");
                    } else {
                        client.gui.setScreen(V2 ? new OreUIV2GalleryScreen() : new OreUIGalleryScreen());
                        LOG.info("[oreui] gallery screen opened{}", V2 ? " (v2)" : "");
                    }
                    captureAt = ticks + 100;
                    stopAt = captureAt + 20;
                } else {
                    client.gui.setScreen(new OreComponentShowcaseScreen());
                    LOG.info("[oreui] showcase screen opened at title");
                }
            }
            if (client.level != null && !galleryOpened) {
                galleryOpened = true;
                boolean useShowcase = SHOWCASE || !SMOKE;
                client.gui.setScreen(useShowcase ? new OreComponentShowcaseScreen()
                        : V2 ? new OreUIV2GalleryScreen() : new OreUIGalleryScreen());
                LOG.info("[oreui] {} screen opened in world",
                        useShowcase ? "showcase" : V2 ? "v2 gallery" : "gallery");
                if (SMOKE) {
                    captureAt = ticks + 20;
                    stopAt = captureAt + 40;
                }
            }
            if (client.gui.screen() instanceof OreUIGalleryScreen gallery) {
                gallery.tickFrame();
            }
            if (client.gui.screen() instanceof OreUIV2GalleryScreen v2Gallery) {
                v2Gallery.tickFrame();
            }
            if (client.gui.screen() instanceof OreComponentShowcaseScreen showcase) {
                showcase.tickFrame();
            }
            if (RESIZE_TEST && client.gui.screen() instanceof OreComponentShowcaseScreen) {
                if (resizeStep == 0 && ticks >= 60) {
                    resizeStep = 1;
                    LOG.info("[oreui] resize test maximize");
                    GLFW.glfwMaximizeWindow(client.getWindow().handle());
                } else if (resizeStep == 1 && ticks >= 140) {
                    resizeStep = 2;
                    logWindow(client, "maximized");
                    grab(client, "oreui-resize-max");
                } else if (resizeStep == 2 && ticks >= 200) {
                    resizeStep = 3;
                    LOG.info("[oreui] resize test restore");
                    GLFW.glfwRestoreWindow(client.getWindow().handle());
                } else if (resizeStep == 3 && ticks >= 280) {
                    resizeStep = 4;
                    logWindow(client, "restored");
                    grab(client, "oreui-resize-restore");
                } else if (resizeStep == 4 && ticks >= 340) {
                    resizeStep = 5;
                    LOG.info("[oreui] resize test -> 640x400");
                    GLFW.glfwSetWindowSize(client.getWindow().handle(), 640, 400);
                } else if (resizeStep == 5 && ticks >= 420) {
                    resizeStep = 6;
                    logWindow(client, "small");
                    grab(client, "oreui-resize-small");
                } else if (resizeStep == 6 && ticks >= 480) {
                    resizeStep = 7;
                    LOG.info("[oreui] resize test -> 320x240");
                    GLFW.glfwSetWindowSize(client.getWindow().handle(), 320, 240);
                } else if (resizeStep == 7 && ticks >= 560) {
                    resizeStep = 8;
                    logWindow(client, "tiny");
                    LOG.info("[oreui] resize test done; stopping");
                    client.stop();
                }
            }
            boolean showcaseKey = InputConstants.isKeyDown(client.getWindow(), InputConstants.KEY_F8);
            if (showcaseKey && !showcaseKeyDown && client.gui.screen() instanceof TitleScreen) {
                client.gui.setScreen(new OreComponentShowcaseScreen());
                LOG.info("[oreui] showcase screen reopened");
            }
            showcaseKeyDown = showcaseKey;
            boolean shotReady = !(client.gui.screen() instanceof OreUIGalleryScreen pendingGallery)
                    || pendingGallery.itemsReady();
            if (SMOKE && !captured && captureAt > 0 && ticks >= captureAt && shotReady) {
                captured = true;
                Screenshot.grab(client.gameDirectory, SHOT_NAME,
                        client.gameRenderer.mainRenderTarget(), 1,
                        component -> LOG.info("[oreui] smoke screenshot: {}", component.getString()));
            }
            if (stopAt > 0 && ticks >= stopAt) {
                LOG.info("[oreui] testmod smoke stop");
                client.stop();
            }
        });
    }

    private static void openSmokeWorld(Minecraft client, Screen parent) {
        LevelSettings settings = new LevelSettings("oreui-smoke", GameType.CREATIVE,
                LevelSettings.DifficultySettings.DEFAULT, false, WorldDataConfiguration.DEFAULT);
        client.createWorldOpenFlows().createFreshLevel("oreui-smoke", settings,
                WorldOptions.testWorldWithRandomSeed(),
                WorldPresets::createNormalWorldDimensions, parent);
    }

    private static void logWindow(Minecraft client, String phase) {
        Screen screen = client.gui.screen();
        LOG.info("[oreui] window {}: physical {}x{}, gui {}x{}, screen {}x{}", phase,
                client.getWindow().getWidth(), client.getWindow().getHeight(),
                client.getWindow().getGuiScaledWidth(), client.getWindow().getGuiScaledHeight(),
                screen == null ? -1 : screen.width, screen == null ? -1 : screen.height);
    }

    private static void grab(Minecraft client, String name) {
        Screenshot.grab(client.gameDirectory, name, client.gameRenderer.mainRenderTarget(), 1,
                component -> LOG.info("[oreui] {} screenshot: {}", name, component.getString()));
    }
}