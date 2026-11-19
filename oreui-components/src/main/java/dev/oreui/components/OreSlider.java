package dev.oreui.components;

import dev.oreui.components.platform.IRenderContext;
import dev.oreui.theme.ColorRole;

public final class OreSlider extends OreInteractive {
    private final SliderMode mode;
    private float value;
    private boolean dragging;

    public OreSlider(ComponentContext context, SliderMode mode) {
        super(context);
        this.mode = mode;
        style().width(180f).height(20f);
    }

    public float value() {
        return value;
    }

    public void setValue(float value) {
        float clamped = Math.max(0f, Math.min(1f, value));
        this.value = mode == SliderMode.DISCRETE_TICKS
                ? Math.round(clamped * 10f) / 10f : clamped;
    }

    @Override
    public boolean pointerDown(float x, float y) {
        if (!isEnabled() || !contains(x, y)) {
            return false;
        }
        dragging = true;
        updateValue(x);
        return true;
    }

    @Override
    public boolean pointerMove(float x, float y) {
        if (!dragging) {
            return false;
        }
        updateValue(x);
        return true;
    }

    @Override
    public boolean pointerUp(float x, float y) {
        if (!dragging) {
            return false;
        }
        dragging = false;
        updateValue(x);
        return true;
    }

    @Override
    public boolean keyPressed(int key, int modifiers) {
        if (!isEnabled()) {
            return false;
        }
        if (key == KeyCodes.LEFT) {
            setValue(value - (mode == SliderMode.DISCRETE_TICKS ? 0.1f : 0.05f));
            return true;
        }
        if (key == KeyCodes.RIGHT) {
            setValue(value + (mode == SliderMode.DISCRETE_TICKS ? 0.1f : 0.05f));
            return true;
        }
        return false;
    }

    private void updateValue(float mouseX) {
        float ratio = (mouseX - bounds().x()) / Math.max(1f, bounds().width());
        setValue(ratio);
    }

    @Override
    public void renderBackground(IRenderContext ctx) {
        float x = bounds().x();
        float y = bounds().y();
        float w = bounds().width();
        float trackY = y + 7f;
        float fillWidth = Math.max(0f, (w - 2f) * value);

        fill(ctx, ColorRole.BORDER_OUTER, x, trackY, w, 6f);
        fill(ctx, ColorRole.SLIDER_TRACK_HIGHLIGHT, x + 1f, trackY + 1f, w - 2f, 2f);
        fill(ctx, ColorRole.SLIDER_TRACK, x + 1f, trackY + 3f, w - 2f, 2f);
        fill(ctx, ColorRole.ACCENT_HIGHLIGHT, x + 1f, trackY + 1f, fillWidth, 2f);
        fill(ctx, ColorRole.ACCENT_DEFAULT, x + 1f, trackY + 3f, fillWidth, 2f);


        float thumbX = x + (w - 14f) * value;
        float thumbY = y + 3f;
        fill(ctx, ColorRole.BORDER_OUTER, thumbX, thumbY, 14f, 14f);
        fill(ctx, ColorRole.SLIDER_THUMB_HIGHLIGHT, thumbX + 1f, thumbY + 1f, 12f, 12f);
        fill(ctx, ColorRole.SLIDER_KNOB, thumbX + 2f, thumbY + 2f, 10f, 9f);
        fill(ctx, ColorRole.SLIDER_THUMB_SHADOW, thumbX + 1f, thumbY + 11f, 12f, 2f);
        drawFocus(ctx);
    }
}