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
        if (mode == ChoiceMode.RADIO_SINGLE) {
            renderRadio(ctx);
        } else {
            renderCheckbox(ctx);
        }
        if (state() == ControlState.FOCUSED) {
            drawFocus(ctx);
        }
    }

    private void renderCheckbox(IRenderContext ctx) {
        float x = bounds().x() + 2f;
        float y = bounds().y() + 2f;
        ColorRole background = selected ? ColorRole.ACCENT_DEFAULT : ColorRole.CHECKBOX_TRACK;
        ColorRole highlight = selected ? ColorRole.ACCENT_HIGHLIGHT : ColorRole.CHECKBOX_TRACK_HIGHLIGHT;
        fill(ctx, background, x, y, 12f, 12f);
        outline(ctx, ColorRole.BORDER_OUTER, x, y, 12f, 12f, 1f);
        fill(ctx, highlight, x + 1f, y + 1f, 10f, 1f);
        fill(ctx, highlight, x + 1f, y + 1f, 1f, 10f);
        fill(ctx, highlight, x + 10f, y + 1f, 1f, 10f);
        fill(ctx, highlight, x + 1f, y + 10f, 10f, 1f);
        if (selected) {
            fill(ctx, ColorRole.TEXT_ON_ACCENT, x + 3f, y + 3f, 6f, 6f);
        }
    }

    private void renderRadio(IRenderContext ctx) {
        float x = bounds().x() + 2f;
        float y = bounds().y() + 2f;
        ColorRole background = selected ? ColorRole.ACCENT_DEFAULT : ColorRole.CHECKBOX_TRACK;
        ColorRole highlight = selected ? ColorRole.ACCENT_HIGHLIGHT : ColorRole.CHECKBOX_TRACK_HIGHLIGHT;
        diamond(ctx, ColorRole.BORDER_OUTER, x, y, 12);
        diamond(ctx, highlight, x + 1f, y + 1f, 10);
        diamond(ctx, background, x + 2f, y + 2f, 8);
        if (selected) {
            diamond(ctx, ColorRole.TEXT_ON_ACCENT, x + 4f, y + 4f, 4);
        }
    }

    private void diamond(IRenderContext ctx, ColorRole role, float x, float y, int size) {
        for (int row = 0; row < size; row++) {
            int inset = Math.min(row, size - 1 - row);
            int start = (size - 1) / 2 - inset;
            fill(ctx, role, x + start, y + row, size - start * 2, 1f);
        }
    }
}