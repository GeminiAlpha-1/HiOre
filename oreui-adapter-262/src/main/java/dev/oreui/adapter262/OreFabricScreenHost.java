package dev.oreui.adapter262;

import dev.oreui.components.platform.IInputSink;
import dev.oreui.components.platform.IRenderContext;
import dev.oreui.components.platform.IScreenHost;
import dev.oreui.components.platform.ITextSource;
import dev.oreui.theme.OreTheme;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class OreFabricScreenHost implements IScreenHost {
    private static final Logger LOG = LoggerFactory.getLogger("oreui-adapter");
    private static final Set<OreFabricScreenHost> HOSTS = new HashSet<>();

    static {
        ScreenEvents.BEFORE_INIT.register((client, screen, width, height) -> {
            for (OreFabricScreenHost host : List.copyOf(HOSTS)) {
                if (host.screen == screen) {
                    host.pendingRebind = true;
                }
            }
        });
        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            for (OreFabricScreenHost host : List.copyOf(HOSTS)) {
                if (host.screen == screen && host.attached && host.pendingRebind) {
                    host.pendingRebind = false;
                    host.bindScreenEvents();
                    LOG.info("[oreui] screen host rebound after resize {}x{}", width, height);
                }
            }
        });
    }

    private final Screen screen;
    private final OreTheme theme;
    private final ITextSource textSource;
    private final RenderCallback callback;
    private final IInputSink inputSink;
    private final Fabric26GameObjectRenderer gameObjectRenderer;
    private Fabric26InputBridge inputBridge;
    private boolean attached;
    private boolean pendingRebind;

    public OreFabricScreenHost(Screen screen, OreTheme theme, ITextSource textSource,
                               RenderCallback callback, IInputSink inputSink,
                               Fabric26GameObjectRenderer gameObjectRenderer) {
        this.screen = screen;
        this.theme = theme;
        this.textSource = textSource;
        this.callback = callback;
        this.inputSink = inputSink;
        this.gameObjectRenderer = gameObjectRenderer;
    }

    @Override
    public void attach() {
        if (attached) {
            return;
        }
        Fabric26Pipelines.initialize();
        bindScreenEvents();
        HOSTS.add(this);
        attached = true;
        LOG.info("[oreui] screen host attached to {}", screen.getClass().getSimpleName());
    }

    private void bindScreenEvents() {
        ScreenEvents.afterBackground(screen).register((s, extractor, mouseX, mouseY, tickDelta) ->
                render(extractor, RenderLayer.BACKGROUND, tickDelta));
        ScreenEvents.afterForeground(screen).register((s, extractor, mouseX, mouseY, tickDelta) ->
                render(extractor, RenderLayer.FOREGROUND, tickDelta));
        if (inputBridge != null) {
            inputBridge.stop();
        }
        inputBridge = new Fabric26InputBridge(screen, inputSink);
    }

    @Override
    public void detach() {
        attached = false;
        pendingRebind = false;
        HOSTS.remove(this);
        if (inputBridge != null) {
            inputBridge.stop();
        }
        LOG.info("[oreui] screen host detached");
    }

    @Override
    public boolean isAttached() {
        return attached;
    }

    private void render(GuiGraphicsExtractor extractor, RenderLayer layer, float tickDelta) {
        if (!attached) {
            return;
        }
        Fabric26RenderContext context = new Fabric26RenderContext(extractor, theme, textSource,
                gameObjectRenderer);
        try {
            context.begin();
            callback.render(context, layer, tickDelta);
        } catch (Throwable throwable) {
            LOG.error("[oreui] render callback failed", throwable);
        } finally {
            context.end();
        }
    }

    public enum RenderLayer {
        BACKGROUND,
        FOREGROUND
    }

    @FunctionalInterface
    public interface RenderCallback {
        void render(IRenderContext context, RenderLayer layer, float tickDelta);
    }
}