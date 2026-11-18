package dev.oreui.components;

import dev.oreui.components.platform.IKeybindBridge;
import dev.oreui.components.platform.IRenderContext;
import dev.oreui.components.platform.IText;
import dev.oreui.components.platform.TextMetrics;
import dev.oreui.theme.ColorRole;
import dev.oreui.theme.TypeRole;

public final class OreKeybind extends OreInteractive {
    private static final float BORDER = 1f;

    private final IKeybindBridge bridge;
    private final KeyConflictPolicy policy;
    private int keyCode;
    private boolean conflict;
    private boolean capturing;

    public OreKeybind(ComponentContext context, KeyConflictPolicy policy) {
        super(context);
        this.bridge = context.keybind();
        this.policy = policy;
        style().height(20f);
    }

    public int keyCode() {
        return keyCode;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    public boolean isConflict() {
        return conflict;
    }

    public void setConflict(boolean conflict) {
        this.conflict = policy == KeyConflictPolicy.WARN && conflict;
    }

    public boolean isCapturing() {
        return capturing;
    }

    @Override
    public boolean pointerDown(float x, float y) {
        if (!isEnabled() || !contains(x, y)) {
            return false;
        }
        capturing = true;
        return true;
    }

    @Override
    public boolean pointerUp(float x, float y) {
        return contains(x, y);
    }

    @Override
    public boolean keyPressed(int key, int modifiers) {
        if (capturing) {
            keyCode = key;
            capturing = false;
            return true;
        }
        return super.keyPressed(key, modifiers);
    }

    @Override
    public void renderBackground(IRenderContext ctx) {
        fill(ctx, ColorRole.SURFACE_CARD, bounds().x(), bounds().y(), bounds().width(), bounds().height());
        ColorRole border = capturing ? ColorRole.FOCUS_RING
                : conflict ? ColorRole.DANGER_DEFAULT : ColorRole.BORDER_OUTER;
        outline(ctx, border, bounds().x(), bounds().y(), bounds().width(), bounds().height(),
                capturing ? 2f : BORDER);
        drawFocus(ctx);
    }

    @Override
    public void renderForeground(IRenderContext ctx) {
        IText label = new LiteralText(capturing ? "…" : bridge.displayName(keyCode));
        TextMetrics metrics = context.textMeasurer().measure(label, TypeRole.LABEL, 0f);
        text(ctx, label, bounds().x() + 6f, bounds().y() + (bounds().height() - metrics.height()) * 0.5f,
                TypeRole.LABEL, conflict ? ColorRole.DANGER_DEFAULT : ColorRole.TEXT_PRIMARY);
        if (conflict) {
            IText mark = new LiteralText("!");
            text(ctx, mark, bounds().right() - 10f,
                    bounds().y() + (bounds().height() - metrics.height()) * 0.5f,
                    TypeRole.LABEL, ColorRole.DANGER_DEFAULT);
        }
    }
}