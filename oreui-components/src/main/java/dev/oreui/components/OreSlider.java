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
        float y = bounds().y() + 7f;
        float w = bounds().width();
        fill(ctx, ColorRole.SURFACE_SUNKEN, x, y, w, 6f);
        outline(ctx, ColorRole.BORDER_OUTER, x, y, w, 6f, 1f);
        fill(ctx, ColorRole.SLIDER_FILL, x + 1f, y + 1f, Math.max(0f, (w - 2f) * value), 4f);
        fill(ctx, ColorRole.BEVEL_LIGHT, x + 1f, y + 1f, Math.max(0f, (w - 2f) * value), 1f);
        if (mode == SliderMode.DISCRETE_TICKS) {
            for (int i = 1; i < 10; i++) {
                float tickX = x + w * i / 10f;
                fill(ctx, ColorRole.BORDER_OUTER, tickX, y + 1f, 1f, 4f);
            }
        }
        float knobX = x + (w - 8f) * value;
        fill(ctx, ColorRole.SLIDER_KNOB, knobX, bounds().y() + 2f, 8f, 12f);
        outline(ctx, ColorRole.BORDER_OUTER, knobX, bounds().y() + 2f, 8f, 12f, 1f);
        drawFocus(ctx);
    }
}