package dev.oreui.adapter262;

import dev.oreui.components.platform.IRenderContext;
import dev.oreui.components.platform.IText;
import dev.oreui.components.platform.ITextSource;
import dev.oreui.core.color.ColorMath;
import dev.oreui.core.geom.Rect2f;
import dev.oreui.core.text.TextGeometry;
import dev.oreui.theme.ColorRole;
import dev.oreui.theme.OreTheme;
import dev.oreui.theme.TypeRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import org.joml.Matrix3x2f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;


public final class Fabric26RenderContext implements IRenderContext {
    private static final Logger LOG = LoggerFactory.getLogger("oreui-adapter");
    private static final Field GUI_STATE = guiStateField();
    private static final AtomicBoolean SDF_LOGGED = new AtomicBoolean();


    private final GuiGraphicsExtractor extractor;
    private final OreTheme theme;
    private final ITextSource textSource;
    private final Rect2f[] clips = new Rect2f[9];
    private final RenderStack stack = new RenderStack();
    private final Fabric26GameObjectRenderer gameObjectRenderer;
    private boolean begun;

    public Fabric26RenderContext(GuiGraphicsExtractor extractor, OreTheme theme, ITextSource textSource,
                                 Fabric26GameObjectRenderer gameObjectRenderer) {
        this.extractor = extractor;
        this.theme = theme;
        this.textSource = textSource;
        this.gameObjectRenderer = gameObjectRenderer;
        for (int i = 0; i < clips.length; i++) {
            clips[i] = new Rect2f();
        }
    }

    @Override
    public void begin() {
        if (begun) {
            throw new IllegalStateException("render context already begun");
        }
        begun = true;
        stack.begin();
        if (gameObjectRenderer != null) {
            gameObjectRenderer.bind(extractor);
        }
        extractor.pose().pushMatrix();
        float oreScale = OreGuiScale.renderScale();
        if (oreScale != 1f) {
            extractor.pose().scale(oreScale, oreScale);
        }
    }

    @Override
    public void end() {
        if (!begun) {
            return;
        }
        while (stack.transformDepth() > 1) {
            extractor.pose().popMatrix();
            stack.popTransform();
        }
        if (stack.transformDepth() == 1) {
            extractor.pose().popMatrix();
        }
        if (stack.clipDepth() > 0) {
            extractor.disableScissor();
        }
        stack.end();
        if (gameObjectRenderer != null) {
            gameObjectRenderer.unbind();
        }
        begun = false;
    }

    @Override
    public void submitPixelRect(Rect2f rect, int argb) {
        requireBegun();
        extractor.fill(Math.round(rect.x()), Math.round(rect.y()),
                Math.round(rect.right()), Math.round(rect.bottom()), applyOpacity(argb));
    }

    @Override
    public void submitSdfRect(Rect2f rect, int argb, float radius, float border) {
        requireBegun();
        if (GUI_STATE == null) {
            throw new IllegalStateException("GuiRenderState access unavailable");
        }
        try {
            GuiRenderState state = (GuiRenderState) GUI_STATE.get(extractor);
            state.addGuiElement(new SdfRectState(Fabric26Pipelines.SDF, new Matrix3x2f(extractor.pose()),
                    rect.x(), rect.y(), rect.right(), rect.bottom(), applyOpacity(argb),
                    Math.round(radius), Math.round(border), currentScissor()));
            if (SDF_LOGGED.compareAndSet(false, true)) {
                LOG.info("[oreui] sdf submit accepted");
            }
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("cannot access GuiRenderState", exception);
        }
    }

    @Override
    public void submitText(IText text, float x, float y, TypeRole role, ColorRole color) {
        submitText(text, x, y, role, color, false);
    }

    @Override
    public void submitText(IText text, float x, float y, TypeRole role, ColorRole color, boolean shadow) {
        requireBegun();
        String resolved = textSource.resolve(text);
        try {
            var font = role == TypeRole.TITLE ? MinecraftFonts.display() : MinecraftFonts.body();
            extractor.text(font, resolved, Math.round(x), Math.round(y),
                    applyOpacity(theme.color(color)), shadow);
        } catch (IllegalStateException exception) {
            // Font providers are being reloaded; skip this frame instead of crashing.
        }
    }

    @Override
    public void submitTextClipped(IText text, float x, float y, TypeRole role, ColorRole color,
                                  Rect2f clip) {
        requireBegun();
        if (clip == null || clip.isEmpty()) {
            return;
        }
        try {
            var font = role == TypeRole.TITLE ? MinecraftFonts.display() : MinecraftFonts.body();
            if (y + font.lineHeight <= clip.y() || y >= clip.bottom()) {
                return;
            }
            float maxWidth = clip.right() - Math.max(x, clip.x());
            if (maxWidth <= 0f) {
                return;
            }
            String resolved = textSource.resolve(text);
            String clipped = font.width(resolved) <= maxWidth ? resolved
                    : TextGeometry.clipToWidth(resolved, maxWidth, value -> font.width(value.toString()));
            if (clipped.isEmpty()) {
                return;
            }
            extractor.text(font, clipped, Math.round(Math.max(x, clip.x())), Math.round(y),
                    applyOpacity(theme.color(color)), false);
        } catch (IllegalStateException exception) {
            // Font providers are being reloaded; skip this frame instead of crashing.
        }
    }

    @Override
    public void pushClip(Rect2f rect) {
        requireBegun();
        stack.pushClip();
        int depth = stack.clipDepth();
        if (depth == 1) {
            int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
            int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();
            clips[0].set(0f, 0f, width, height);
        }
        Rect2f current = clips[depth - 1];
        Rect2f next = clips[depth];
        next.set(current);
        if (!next.intersect(rect)) {
            next.set(0f, 0f, 0f, 0f);
        }
        enable(next);
    }

    @Override
    public void popClip() {
        requireBegun();
        stack.popClip();
        int depth = stack.clipDepth();
        if (depth == 0) {
            extractor.disableScissor();
        } else {
            enable(clips[depth]);
        }
    }

    @Override
    public void pushTransform(float translateX, float translateY, float scale) {
        requireBegun();
        extractor.pose().pushMatrix();
        extractor.pose().translate(translateX, translateY);
        extractor.pose().scale(scale, scale);
        stack.pushTransform();
    }

    @Override
    public void popTransform() {
        requireBegun();
        stack.popTransform();
        extractor.pose().popMatrix();
    }

    @Override
    public void pushOpacity(float alpha) {
        requireBegun();
        stack.pushOpacity(alpha);
    }

    @Override
    public void popOpacity() {
        requireBegun();
        stack.popOpacity();
    }

    private void requireBegun() {
        if (!begun) {
            throw new IllegalStateException("render context not begun");
        }
    }

    private void enable(Rect2f rect) {
        float scale = OreGuiScale.renderScale();
        extractor.enableScissor(Math.round(rect.x() * scale), Math.round(rect.y() * scale),
                Math.round(rect.right() * scale), Math.round(rect.bottom() * scale));
    }

    private ScreenRectangle currentScissor() {
        int depth = stack.clipDepth();
        if (depth == 0) {
            return null;
        }
        Rect2f clip = clips[depth];
        return new ScreenRectangle(Math.round(clip.x()), Math.round(clip.y()),
                Math.round(clip.width()), Math.round(clip.height()));
    }

    private int applyOpacity(int argb) {
        int sourceAlpha = (argb >>> 24) & 0xFF;
        int alpha = Math.round(sourceAlpha * stack.opacity());
        return ColorMath.withAlpha(argb, alpha);
    }

    private static Field guiStateField() {
        try {
            Field field = GuiGraphicsExtractor.class.getDeclaredField("guiRenderState");
            field.setAccessible(true);
            return field;
        } catch (ReflectiveOperationException exception) {
            LOG.error("[oreui] cannot access GuiRenderState", exception);
            return null;
        }
    }
}