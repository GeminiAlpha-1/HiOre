package dev.oreui.components;

import dev.oreui.components.platform.IRenderContext;
import dev.oreui.components.platform.IText;
import dev.oreui.components.platform.TextMetrics;
import dev.oreui.theme.ColorRole;
import dev.oreui.theme.TypeRole;

import java.util.List;

public final class OreTabs extends OreInteractive {
    private final Orientation orientation;
    private final List<IText> labels;
    private int selected;

    public OreTabs(ComponentContext context, Orientation orientation, List<IText> labels) {
        super(context);
        this.orientation = orientation;
        this.labels = List.copyOf(labels);
        style().width(orientation == Orientation.HORIZONTAL ? 180f : 22f);
        style().height(orientation == Orientation.HORIZONTAL ? 22f : 110f);
    }

    public int selected() {
        return selected;
    }

    public void select(int index) {
        if (index >= 0 && index < labels.size()) {
            selected = index;
        }
    }

    @Override
    public boolean pointerDown(float x, float y) {
        if (!isEnabled() || !contains(x, y)) {
            return false;
        }
        int index = orientation == Orientation.HORIZONTAL
                ? (int) ((x - bounds().x()) / Math.max(1f, bounds().width() / labels.size()))
                : (int) ((y - bounds().y()) / Math.max(1f, bounds().height() / labels.size()));
        select(Math.max(0, Math.min(labels.size() - 1, index)));
        return true;
    }

    @Override
    public void renderBackground(IRenderContext ctx) {
        fill(ctx, ColorRole.SURFACE_CARD, bounds().x(), bounds().y(), bounds().width(), bounds().height());
        outline(ctx, ColorRole.BORDER_OUTER, bounds().x(), bounds().y(), bounds().width(), bounds().height(), 1f);
        if (orientation == Orientation.HORIZONTAL) {
            float tabWidth = bounds().width() / labels.size();
            fill(ctx, ColorRole.ACCENT_DEFAULT, bounds().x() + selected * tabWidth,
                    bounds().y() + bounds().height() - 2f, tabWidth, 2f);
        } else {
            float tabHeight = bounds().height() / labels.size();
            fill(ctx, ColorRole.ACCENT_DEFAULT, bounds().x(), bounds().y() + selected * tabHeight, 2f, tabHeight);
        }
        drawFocus(ctx);
    }

    @Override
    public void renderForeground(IRenderContext ctx) {
        for (int i = 0; i < labels.size(); i++) {
            IText label = labels.get(i);
            TextMetrics metrics = context.textMeasurer().measure(label, TypeRole.LABEL, 0f);
            float x;
            float y;
            if (orientation == Orientation.HORIZONTAL) {
                float tabWidth = bounds().width() / labels.size();
                x = bounds().x() + i * tabWidth + (tabWidth - metrics.width()) * 0.5f;
                y = bounds().y() + (bounds().height() - metrics.height()) * 0.5f;
            } else {
                float tabHeight = bounds().height() / labels.size();
                x = bounds().x() + 6f;
                y = bounds().y() + i * tabHeight + (tabHeight - metrics.height()) * 0.5f;
            }
            text(ctx, label, x, y, TypeRole.LABEL,
                    i == selected ? ColorRole.TEXT_PRIMARY : ColorRole.TEXT_MUTED);
        }
    }
}