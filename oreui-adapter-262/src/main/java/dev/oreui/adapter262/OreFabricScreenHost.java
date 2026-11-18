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

public final class OreFabricScreenHost implements IScreenHost {
    private static final Logger LOG = LoggerFactory.getLogger("oreui-adapter");

    private final Screen screen;
    private final OreTheme theme;
    private final ITextSource textSource;
    private final RenderCallback callback;
    private final IInputSink inputSink;
    private final Fabric26GameObjectRenderer gameObjectRenderer;
    private Fabric26InputBridge inputBridge;
    private boolean attached;

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
        ScreenEvents.afterBackground(screen).register((s, extractor, mouseX, mouseY, tickDelta) ->
                render(extractor, RenderLayer.BACKGROUND, tickDelta));
        ScreenEvents.afterForeground(screen).register((s, extractor, mouseX, mouseY, tickDelta) ->
                render(extractor, RenderLayer.FOREGROUND, tickDelta));

        inputBridge = new Fabric26InputBridge(screen, inputSink);
        attached = true;
        LOG.info("[oreui] screen host attached to {}", screen.getClass().getSimpleName());
    }

    @Override
    public void detach() {
        attached = false;
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