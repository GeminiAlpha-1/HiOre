package dev.oreui.components;

import dev.oreui.components.platform.IRenderContext;
import dev.oreui.components.platform.IText;
import dev.oreui.components.platform.TextMetrics;
import dev.oreui.theme.ButtonVariant;
import dev.oreui.theme.ColorRole;
import dev.oreui.theme.ControlState;
import dev.oreui.theme.StateStyle;
import dev.oreui.theme.TypeRole;

public final class OreButton extends OreInteractive {
    private final ButtonVariant variant;
    private final ControlSize size;
    private IText text;
    private final Runnable action;

    public OreButton(ComponentContext context, ButtonVariant variant, ControlSize size,
                     IText text, Runnable action) {
        super(context);
        this.variant = variant;
        this.size = size;
        this.text = text;
        this.action = action == null ? () -> {
        } : action;
        style().height(size.height());
    }

    public void setText(IText text) {
        this.text = text;
    }

    @Override
    public void renderBackground(IRenderContext ctx) {
        StateStyle state = context.theme().state(variant, state());
        float x = bounds().x();
        float y = bounds().y() + state.offsetY();
        float w = bounds().width();
        float h = bounds().height();
        fill(ctx, state.background(), x, y, w, h);
        if (variant != ButtonVariant.GHOST) {
            outline(ctx, ColorRole.BORDER_OUTER, x, y, w, h, 1f);
            fill(ctx, state.bevelLight(), x + 1f, y + 1f, w - 2f, 1f);
            fill(ctx, state.bevelLight(), x + 1f, y + 1f, 1f, h - 2f);
            fill(ctx, state.bevelDark(), x + 1f, y + h - 2f, w - 2f, 1f);
            fill(ctx, state.bevelDark(), x + w - 2f, y + 1f, 1f, h - 2f);
        }
        if (state.lipHeight() > 0) {
            fill(ctx, state.lip(), x + 1f, y + h - state.lipHeight(), w - 2f, state.lipHeight());
        }
        drawFocus(ctx);
    }

    @Override
    public void renderForeground(IRenderContext ctx) {
        StateStyle state = context.theme().state(variant, state());
        TextMetrics metrics = context.textMeasurer().measure(text, TypeRole.LABEL, 0f);
        float x = bounds().x() + (bounds().width() - metrics.width()) * 0.5f;
        float y = bounds().y() + state.offsetY() + (bounds().height() - metrics.height()) * 0.5f;
        text(ctx, text, x, y, TypeRole.LABEL, state.text());
    }

    @Override
    protected void activate() {
        action.run();
        context.sound().play(dev.oreui.theme.SoundRole.CLICK, 1f, 1f);
    }

    public ButtonVariant variant() {
        return variant;
    }

    public ControlSize size() {
        return size;
    }
}