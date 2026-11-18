package dev.oreui.components;

import dev.oreui.components.platform.IRenderContext;
import dev.oreui.components.platform.IText;
import dev.oreui.components.platform.TextMetrics;
import dev.oreui.theme.ColorRole;
import dev.oreui.theme.TypeRole;

import java.util.List;

public final class OreSegmentedControl extends OreInteractive {
    private static final float BORDER = 1f;
    private static final float LIP = 2f;

    private final Orientation orientation;
    private final List<IText> labels;
    private int selected;

    public OreSegmentedControl(ComponentContext context, Orientation orientation, List<IText> labels) {
        super(context);
        this.orientation = orientation;
        this.labels = List.copyOf(labels);
        if (orientation == Orientation.HORIZONTAL) {
            style().width(180f).height(22f);
        } else {
            style().width(22f).height(110f);
        }
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
        if (!isEnabled() || !contains(x, y) || labels.isEmpty()) {
            return false;
        }
        int index = orientation == Orientation.HORIZONTAL
                ? (int) ((x - bounds().x()) / Math.max(1f, bounds().width() / labels.size()))
                : (int) ((y - bounds().y()) / Math.max(1f, bounds().height() / labels.size()));
        select(Math.max(0, Math.min(labels.size() - 1, index)));
        return true;
    }

    @Override
    public boolean pointerUp(float x, float y) {
        return contains(x, y);
    }

    @Override
    public void renderBackground(IRenderContext ctx) {
        float x = bounds().x();
        float y = bounds().y();
        float w = bounds().width();
        float h = bounds().height();
        fill(ctx, ColorRole.SURFACE_SUNKEN, x, y, w, h);
        fill(ctx, ColorRole.WELL_SHADOW, x, y, w, 2f);
        fill(ctx, ColorRole.WELL_RIM, x, y + h - BORDER, w, BORDER);
        outline(ctx, ColorRole.BORDER_OUTER, x, y, w, h, BORDER);
        if (labels.isEmpty()) {
            drawFocus(ctx);
            return;
        }
        float segment = orientation == Orientation.HORIZONTAL ? w / labels.size() : h / labels.size();
        for (int i = 0; i < labels.size(); i++) {
            float sx = orientation == Orientation.HORIZONTAL ? x + i * segment : x;
            float sy = orientation == Orientation.HORIZONTAL ? y : y + i * segment;
            float sw = orientation == Orientation.HORIZONTAL ? segment : w;
            float sh = orientation == Orientation.HORIZONTAL ? h : segment;
            if (i > 0) {
                float lineX = orientation == Orientation.HORIZONTAL ? sx : x + BORDER;
                float lineY = orientation == Orientation.HORIZONTAL ? y + BORDER : sy;
                float lineW = orientation == Orientation.HORIZONTAL ? BORDER : w - BORDER * 2f;
                float lineH = orientation == Orientation.HORIZONTAL ? h - BORDER * 2f : BORDER;
                fill(ctx, ColorRole.BORDER_OUTER, lineX, lineY, lineW, lineH);
            }
            if (i == selected) {
                fill(ctx, ColorRole.SURFACE_RAISED_DEFAULT, sx + BORDER, sy + BORDER,
                        sw - BORDER * 2f, sh - BORDER * 2f - LIP);
                fill(ctx, ColorRole.BEVEL_LIGHT, sx + BORDER, sy + BORDER, sw - BORDER * 2f, BORDER);
                fill(ctx, ColorRole.BEVEL_LIGHT, sx + BORDER, sy + BORDER, BORDER, sh - BORDER * 2f);
                fill(ctx, ColorRole.BEVEL_DARK, sx + BORDER, sy + sh - BORDER - LIP,
                        sw - BORDER * 2f, BORDER);
                fill(ctx, ColorRole.ACCENT_LIP, sx + BORDER, sy + sh - BORDER - LIP,
                        sw - BORDER * 2f, LIP);
            }
        }
        drawFocus(ctx);
    }

    @Override
    public void renderForeground(IRenderContext ctx) {
        if (labels.isEmpty()) {
            return;
        }
        float segment = orientation == Orientation.HORIZONTAL
                ? bounds().width() / labels.size() : bounds().height() / labels.size();
        for (int i = 0; i < labels.size(); i++) {
            TextMetrics metrics = context.textMeasurer().measure(labels.get(i), TypeRole.LABEL, 0f);
            float x;
            float y;
            if (orientation == Orientation.HORIZONTAL) {
                x = bounds().x() + i * segment + (segment - metrics.width()) * 0.5f;
                y = bounds().y() + (bounds().height() - metrics.height()) * 0.5f;
            } else {
                x = bounds().x() + (bounds().width() - metrics.width()) * 0.5f;
                y = bounds().y() + i * segment + (segment - metrics.height()) * 0.5f;
            }
            text(ctx, labels.get(i), x, y, TypeRole.LABEL,
                    i == selected ? ColorRole.TEXT_PRIMARY : ColorRole.TEXT_MUTED);
        }
    }
}