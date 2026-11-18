package dev.oreui.components;

import dev.oreui.components.platform.IRenderContext;
import dev.oreui.components.platform.IText;
import dev.oreui.theme.ColorRole;
import dev.oreui.theme.TypeRole;

public final class OreToast extends OreWidget {
    private final IText text;
    private final ToastSeverity severity;
    private float remaining;

    public OreToast(ComponentContext context, IText text, ToastSeverity severity) {
        super(context);
        this.text = text;
        this.severity = severity;
        style().width(240f).height(36f);
    }

    public void show(float seconds) {
        remaining = seconds;
    }

    public float remaining() {
        return remaining;
    }

    @Override
    public void update(float deltaSeconds) {
        remaining = Math.max(0f, remaining - deltaSeconds);
    }

    @Override
    public void renderBackground(IRenderContext ctx) {
        if (remaining <= 0f) {
            return;
        }
        fill(ctx, ColorRole.SURFACE_CARD, bounds().x(), bounds().y(), bounds().width(), bounds().height());
        outline(ctx, ColorRole.BORDER_OUTER, bounds().x(), bounds().y(), bounds().width(), bounds().height(), 1f);
        fill(ctx, accent(), bounds().x(), bounds().y(), 4f, bounds().height());
    }

    @Override
    public void renderForeground(IRenderContext ctx) {
        if (remaining <= 0f) {
            return;
        }
        text(ctx, text, bounds().x() + 10f, bounds().y() + 12f, TypeRole.LABEL, ColorRole.TEXT_PRIMARY);
    }

    private ColorRole accent() {
        return switch (severity) {
            case NEUTRAL -> ColorRole.SURFACE_RAISED_DEFAULT;
            case INFO -> ColorRole.TOAST_INFO_ACCENT;
            case SUCCESS -> ColorRole.TOAST_SUCCESS_ACCENT;
            case WARNING -> ColorRole.TOAST_WARNING_ACCENT;
            case ERROR -> ColorRole.TOAST_ERROR_ACCENT;
        };
    }
}