package dev.oreui.components;

import dev.oreui.components.platform.IRenderContext;
import dev.oreui.theme.ColorRole;
import dev.oreui.theme.SoundRole;

public final class OreToggle extends OreInteractive {
    private boolean on;

    public OreToggle(ComponentContext context) {
        super(context);
        style().width(36f).height(16f);
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    @Override
    protected void activate() {
        setOn(!on);
        context.sound().play(on ? SoundRole.TOGGLE_ON : SoundRole.TOGGLE_OFF, 1f, 1f);
    }

    @Override
    public void renderBackground(IRenderContext ctx) {
        float ox = bounds().x() + (bounds().width() - 28f) * 0.5f;
        float oy = bounds().y() + (bounds().height() - 15f) * 0.5f;
        float trackX = on ? ox : ox + 15f;
        float trackY = oy + 1f;
        fill(ctx, ColorRole.BORDER_OUTER, trackX, trackY, 13f, 13f);
        fill(ctx, on ? ColorRole.ACCENT_HIGHLIGHT : ColorRole.SWITCH_TRACK_HIGHLIGHT,
                trackX + 1f, trackY + 1f, 11f, 11f);
        fill(ctx, on ? ColorRole.ACCENT_DEFAULT : ColorRole.SWITCH_TRACK,
                trackX + 2f, trackY + 2f, 9f, 9f);

        float thumbX = on ? ox + 13f : ox;
        fill(ctx, ColorRole.BORDER_OUTER, thumbX, oy, 15f, 15f);
        fill(ctx, ColorRole.SWITCH_THUMB_HIGHLIGHT, thumbX + 1f, oy + 1f, 13f, 13f);
        fill(ctx, ColorRole.SWITCH_THUMB, thumbX + 2f, oy + 2f, 11f, 10f);
        fill(ctx, ColorRole.SWITCH_THUMB_SHADOW, thumbX + 1f, oy + 12f, 13f, 2f);
        drawFocus(ctx);
    }
}