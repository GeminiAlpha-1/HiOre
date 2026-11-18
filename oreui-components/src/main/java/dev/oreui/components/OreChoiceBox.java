package dev.oreui.components;

import dev.oreui.components.platform.IRenderContext;
import dev.oreui.theme.ColorRole;
import dev.oreui.theme.ControlState;
import dev.oreui.theme.SoundRole;

public final class OreChoiceBox extends OreInteractive {
    private final ChoiceMode mode;
    private boolean selected;

    public OreChoiceBox(ComponentContext context, ChoiceMode mode) {
        super(context);
        this.mode = mode;
        style().width(16f).height(16f);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    protected void activate() {
        if (mode == ChoiceMode.RADIO_SINGLE) {
            selected = true;
        } else {
            selected = !selected;
        }
        context.sound().play(selected ? SoundRole.TOGGLE_ON : SoundRole.TOGGLE_OFF, 1f, 1f);
    }

    @Override
    public void renderBackground(IRenderContext ctx) {
        float x = bounds().x();
        float y = bounds().y();
        fill(ctx, ColorRole.SURFACE_SUNKEN, x, y, 16f, 16f);
        outline(ctx, ColorRole.BORDER_OUTER, x, y, 16f, 16f, 1f);
        if (selected) {
            fill(ctx, ColorRole.ACCENT_DEFAULT, x + 2f, y + 2f, 12f, 12f);
            if (mode == ChoiceMode.RADIO_SINGLE) {
                fill(ctx, ColorRole.ACCENT_LIP, x + 6f, y + 6f, 4f, 4f);
            } else {
                fill(ctx, ColorRole.TEXT_ON_ACCENT, x + 4f, y + 7f, 2f, 2f);
                fill(ctx, ColorRole.TEXT_ON_ACCENT, x + 6f, y + 9f, 2f, 2f);
                fill(ctx, ColorRole.TEXT_ON_ACCENT, x + 8f, y + 5f, 2f, 2f);
                fill(ctx, ColorRole.TEXT_ON_ACCENT, x + 10f, y + 3f, 2f, 2f);
            }
        }
        if (state() == ControlState.FOCUSED) {
            drawFocus(ctx);
        }
    }
}