package dev.oreui.components;

import dev.oreui.components.platform.IRenderContext;
import dev.oreui.components.platform.IText;
import dev.oreui.components.platform.TextMetrics;
import dev.oreui.core.text.TextGeometry;
import dev.oreui.core.text.TextMeasure;
import dev.oreui.theme.ButtonVariant;
import dev.oreui.theme.ColorRole;
import dev.oreui.theme.ControlState;
import dev.oreui.theme.Space;
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
        float y = bounds().y();
        float w = bounds().width();
        float h = bounds().height();
        int offset = state.offsetY();
        if (variant != ButtonVariant.GHOST) {
            float faceTop = y + offset;
            float faceHeight = h - offset;
            fill(ctx, state.background(), x + 1f, faceTop + 1f, w - 2f, faceHeight - 2f);
            fill(ctx, state.bevelLight(), x + 1f, faceTop + 1f, w - 2f, 1f);
            float bottomHighlight = y + h - 3f + offset;
            float railTop = faceTop + 2f;
            float railBottom = Math.min(y + h - 1f, bottomHighlight);
            fill(ctx, state.bevelLight(), x + 1f, railTop, 1f, railBottom - railTop);
            fill(ctx, state.bevelLight(), x + w - 2f, railTop, 1f, railBottom - railTop);
            if (bottomHighlight < y + h - 1f) {
                fill(ctx, state.bevelLight(), x + 1f, bottomHighlight, w - 2f, 1f);
            }
            if (offset == 0 && state.lipHeight() > 0) {
                fill(ctx, state.lip(), x + 1f, y + h - state.lipHeight(), w - 2f, state.lipHeight());
            }
            fill(ctx, ColorRole.BORDER_OUTER, x, faceTop, w, 1f);
            fill(ctx, ColorRole.BORDER_OUTER, x, y + h - 1f, w, 1f);
            fill(ctx, ColorRole.BORDER_OUTER, x, faceTop + 1f, 1f, faceHeight - 2f);
            fill(ctx, ColorRole.BORDER_OUTER, x + w - 1f, faceTop + 1f, 1f, faceHeight - 2f);
        } else {
            fill(ctx, state.background(), x, y, w, h);
        }
        drawFocus(ctx);
    }

    @Override
    public void renderForeground(IRenderContext ctx) {
        StateStyle state = context.theme().state(variant, state());
        TypeRole fontRole = variant == ButtonVariant.HERO ? TypeRole.TITLE : TypeRole.LABEL;
        float padding = context.theme().space(Space.XS);
        float maxWidth = Math.max(1f, bounds().width() - padding * 2f);
        TextMeasure measure = value -> context.textMeasurer()
                .measure(new LiteralText(value.toString()), fontRole, 0f).width();
        String clipped = TextGeometry.ellipsize(context.textSource().resolve(text), maxWidth, measure);
        if (variant == ButtonVariant.HERO) {
            clipped = asciiOnly(clipped);
        }
        TextMetrics metrics = context.textMeasurer()
                .measure(new LiteralText(clipped), fontRole, 0f);
        float x = bounds().x() + (bounds().width() - metrics.width()) * 0.5f;
        int offset = state.offsetY();
        float innerTop = bounds().y() + offset + 2f;
        float innerBottom = Math.min(bounds().y() + bounds().height() - 1f,
                bounds().y() + bounds().height() - 3f + offset);
        float y = textCenterY(innerTop, Math.max(1f, innerBottom - innerTop), metrics);
        if (variant == ButtonVariant.HERO) {
            textClipped(ctx, new LiteralText(clipped), x + 2f, y + 2f, fontRole,
                    ColorRole.TEXT_SHADOW, bounds());
        }
        textClipped(ctx, new LiteralText(clipped), x, y, fontRole, state.text(), bounds());
    }

    private static String asciiOnly(String value) {
        boolean ascii = true;
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) > 0x7E) {
                ascii = false;
                break;
            }
        }
        if (ascii) {
            return value;
        }
        StringBuilder filtered = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            filtered.append(c >= 0x20 && c <= 0x7E ? c : '?');
        }
        return filtered.toString();
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