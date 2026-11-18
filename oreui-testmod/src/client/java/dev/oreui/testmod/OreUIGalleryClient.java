package dev.oreui.testmod;

import dev.oreui.adapter262.Fabric26Clipboard;
import dev.oreui.adapter262.Fabric26GameObjectRenderer;
import dev.oreui.adapter262.Fabric26KeybindBridge;
import dev.oreui.adapter262.Fabric26Pipelines;
import dev.oreui.adapter262.Fabric26SoundBridge;
import dev.oreui.adapter262.Fabric26TextMeasurer;
import dev.oreui.adapter262.Fabric26TextSource;
import dev.oreui.adapter262.Fabric26TimeSource;
import dev.oreui.adapter262.OreFabricScreenHost;
import dev.oreui.api.OreUI;
import dev.oreui.components.BadgeSeverity;
import dev.oreui.components.ComponentContext;
import dev.oreui.components.ComponentTree;
import dev.oreui.components.DropdownSize;
import dev.oreui.components.LabelOverflow;
import dev.oreui.components.LiteralText;
import dev.oreui.components.ModalSize;
import dev.oreui.components.OreBadge;
import dev.oreui.components.OreDropdown;
import dev.oreui.components.OreLabel;
import dev.oreui.components.OreModal;
import dev.oreui.components.OreScrollPane;
import dev.oreui.components.OreSpinner;
import dev.oreui.components.OreSurface;
import dev.oreui.components.OreTabs;
import dev.oreui.components.OreToast;
import dev.oreui.components.OreTooltip;
import dev.oreui.components.Orientation;
import dev.oreui.components.ScrollBarPolicy;
import dev.oreui.components.SurfaceVariant;
import dev.oreui.components.ToastSeverity;
import dev.oreui.components.TooltipPlacement;
import dev.oreui.core.geom.Insets;
import dev.oreui.core.geom.Rect2f;
import dev.oreui.theme.OreTokens;
import dev.oreui.theme.TypeRole;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class OreUIGalleryClient implements ClientModInitializer {
    private static final Logger LOG = LoggerFactory.getLogger("oreui-testmod");
    private static final boolean SMOKE = Boolean.parseBoolean(System.getProperty("orui.smoke", "false"));
    private static final String SHOT_NAME = System.getProperty("orui.shot", "oreui-gallery-smoke");
    private static final boolean WORLD = Boolean.parseBoolean(System.getProperty("orui.world", "false"));
    private static final boolean V2 = Boolean.parseBoolean(System.getProperty("orui.v2", "false"));

    private int ticks;
    private int stopAt = -1;
    private int captureAt = -1;
    private boolean titleSeen;
    private boolean captured;
    private boolean galleryOpened;
    private ComponentTree tree;
    private OreFabricScreenHost host;

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
                    client.gui.setScreen(V2 ? new OreUIV2GalleryScreen() : new OreUIGalleryScreen());
                    LOG.info("[oreui] gallery screen opened{}", V2 ? " (v2)" : "");
                    captureAt = ticks + 100;
                    stopAt = captureAt + 20;
                } else {
                    attachHost(client);
                    LOG.info("[oreui] testmod reached TitleScreen");
                }
            }
            if (client.level != null && !galleryOpened) {
                galleryOpened = true;
                client.gui.setScreen(V2 ? new OreUIV2GalleryScreen() : new OreUIGalleryScreen());
                LOG.info("[oreui] gallery screen opened in world{}", V2 ? " (v2)" : "");
                captureAt = ticks + 20;
                stopAt = captureAt + 40;
            }
            if (client.gui.screen() instanceof OreUIGalleryScreen gallery) {
                gallery.tickFrame();
            }
            if (client.gui.screen() instanceof OreUIV2GalleryScreen v2Gallery) {
                v2Gallery.tickFrame();
            }
            if (tree != null) {
                tree.layout(new Rect2f(6f, 6f, 300f, 232f));
                tree.update(1f / 20f);
            }
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

    private void attachHost(Minecraft client) {
        if (host != null) {
            return;
        }
        Fabric26TextSource textSource = new Fabric26TextSource();
        Fabric26TextMeasurer measurer = new Fabric26TextMeasurer(client.font, textSource, OreTokens.DARK);
        Fabric26GameObjectRenderer gameObjects = new Fabric26GameObjectRenderer();
        ComponentContext context = new ComponentContext(OreTokens.DARK, textSource, measurer,
                new Fabric26Clipboard(), new Fabric26SoundBridge(), new Fabric26KeybindBridge(),
                new Fabric26TimeSource(), gameObjects);
        OreSurface root = new OreSurface(context, SurfaceVariant.PANEL);
        root.style().width(300f).height(232f).padding(new Insets(4f, 4f, 4f, 4f)).gap(2f);

        OreTabs tabs = new OreTabs(context, Orientation.HORIZONTAL,
                List.of(new LiteralText("One"), new LiteralText("Two"), new LiteralText("Three")));
        tabs.style().width(284f).height(18f);
        root.addChild(tabs);
        OreDropdown dropdown = new OreDropdown(context, DropdownSize.STANDARD,
                List.of(new LiteralText("Alpha"), new LiteralText("Beta"), new LiteralText("Gamma")));
        dropdown.style().width(284f).height(16f);
        dropdown.setOpen(true);
        root.addChild(dropdown);

        OreSurface content = new OreSurface(context, SurfaceVariant.CARD);
        content.style().width(270f).height(130f).padding(new Insets(4f, 4f, 4f, 4f)).gap(2f);
        for (int i = 0; i < 5; i++) {
            content.addChild(new OreLabel(context, new LiteralText("Scroll row " + (i + 1)),
                    TypeRole.BODY, LabelOverflow.CLIP, 250));
        }
        OreScrollPane scroll = new OreScrollPane(context, content, ScrollBarPolicy.ALWAYS);
        scroll.style().width(284f).height(36f);
        scroll.scrollBy(20f);
        root.addChild(scroll);

        OreModal modal = new OreModal(context, new LiteralText("Modal"), new LiteralText("Phase 7 overlay"),
                ModalSize.DIALOG_MD);
        modal.setOpen(true);
        modal.style().width(200f).height(60f);
        root.addChild(modal);
        OreTooltip tooltip = new OreTooltip(context, new LiteralText("Tooltip"), TooltipPlacement.BOTTOM);
        tooltip.style().height(18f);
        root.addChild(tooltip);
        OreToast toast = new OreToast(context, new LiteralText("Saved"), ToastSeverity.SUCCESS);
        toast.show(10f);
        toast.style().width(284f).height(24f);
        root.addChild(toast);
        OreSpinner spinner = new OreSpinner(context);
        spinner.style().width(10f).height(10f);
        root.addChild(spinner);
        OreBadge badge = new OreBadge(context, new LiteralText("NEW"), BadgeSeverity.INFO);
        badge.style().width(80f).height(10f);
        root.addChild(badge);

        tree = new ComponentTree(root);
        host = new OreFabricScreenHost(client.gui.screen(), OreTokens.DARK, textSource,
                (ctx, layer, tickDelta) -> {
                    if (layer == OreFabricScreenHost.RenderLayer.FOREGROUND) {
                        tree.renderBackground(ctx);
                        tree.renderForeground(ctx);
                    }
                }, event -> tree.dispatch(event), gameObjects);
        host.attach();
        LOG.info("[oreui] phase7 component tree attached");
    }
}