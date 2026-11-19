package dev.oreui.components;

import dev.oreui.components.platform.IRenderContext;
import dev.oreui.components.platform.IText;
import dev.oreui.components.platform.TextMetrics;
import dev.oreui.theme.ColorRole;
import dev.oreui.theme.TypeRole;

public final class OreBadge extends OreWidget {
    private final IText text;
    private final BadgeSeverity severity;

    public OreBadge(ComponentContext context, IText text, BadgeSeverity severity) {
        super(context);
        this.text = text;
        this.severity = severity;
        style().width(80f).height(14f);
    }

    @Override
    public void renderBackground(IRenderContext ctx) {
        fill(ctx, background(), bounds().x(), bounds().y(), bounds().width(), bounds().height());
        outline(ctx, ColorRole.BORDER_OUTER, bounds().x(), bounds().y(), bounds().width(), bounds().height(), 1f);
    }

    @Override
    public void renderForeground(IRenderContext ctx) {
        TextMetrics metrics = context.textMeasurer().measure(text, TypeRole.LABEL, 0f);
        text(ctx, text, bounds().x() + 4f,
                textCenterY(bounds().y() + 1f, Math.max(1f, bounds().height() - 2f), metrics),
                TypeRole.LABEL, ColorRole.TEXT_ON_ACCENT);
    }

    private ColorRole background() {
        return switch (severity) {
            case NEUTRAL -> ColorRole.SURFACE_RAISED_DEFAULT;
            case INFO -> ColorRole.TOAST_INFO_ACCENT;
            case SUCCESS -> ColorRole.TOAST_SUCCESS_ACCENT;
            case WARNING -> ColorRole.TOAST_WARNING_ACCENT;
            case ERROR -> ColorRole.TOAST_ERROR_ACCENT;
        };
    }
}