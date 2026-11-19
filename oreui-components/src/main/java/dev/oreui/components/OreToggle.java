package dev.oreui.components;

import dev.oreui.components.platform.IRenderContext;
import dev.oreui.core.anim.Easings;
import dev.oreui.core.anim.Tween;
import dev.oreui.theme.ColorRole;
import dev.oreui.theme.ControlState;
import dev.oreui.theme.Motion;
import dev.oreui.theme.SoundRole;

public final class OreToggle extends OreInteractive {
    private boolean on;
    private final Tween knob = new Tween(0f, Motion.FAST.millis() / 1000f, Easings.ORE_STANDARD);

    public OreToggle(ComponentContext context) {
        super(context);
        style().width(36f).height(16f);
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
        knob.snapToTarget();
        knob.setTarget(on ? 1f : 0f);
    }

    @Override
    public void update(float deltaSeconds) {
        knob.advance(deltaSeconds);
        super.update(deltaSeconds);
    }

    @Override
    protected void activate() {
        setOn(!on);
        context.sound().play(on ? SoundRole.TOGGLE_ON : SoundRole.TOGGLE_OFF, 1f, 1f);
    }

    @Override
    public void renderBackground(IRenderContext ctx) {
        float x = bounds().x();
        float y = bounds().y();
        float w = bounds().width();
        float h = bounds().height();
        fill(ctx, on ? ColorRole.ACCENT_DEFAULT : ColorRole.SURFACE_SUNKEN, x, y, w, h);
        outline(ctx, ColorRole.BORDER_OUTER, x, y, w, h, 1f);
        fill(ctx, ColorRole.BEVEL_LIGHT, x + 1f, y + 1f, w - 2f, 1f);
        fill(ctx, ColorRole.BEVEL_DARK, x + 1f, y + h - 2f, w - 2f, 1f);
        float knobX = x + 2f + (w - 14f) * knob.value();
        float knobY = y + 2f;
        fill(ctx, ColorRole.SURFACE_RAISED_DEFAULT, knobX, knobY, 10f, h - 4f);
        outline(ctx, ColorRole.BORDER_OUTER, knobX, knobY, 10f, h - 4f, 1f);
        fill(ctx, ColorRole.BEVEL_LIGHT, knobX + 1f, knobY + 1f, 8f, 1f);
        fill(ctx, ColorRole.BEVEL_DARK, knobX + 1f, knobY + h - 6f, 8f, 1f);
        drawFocus(ctx);
    }
}