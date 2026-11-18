package dev.oreui.components;

import dev.oreui.components.platform.IText;
import dev.oreui.components.platform.TextMetrics;
import dev.oreui.theme.ColorRole;
import dev.oreui.theme.TypeRole;

import java.util.ArrayList;
import java.util.List;

public final class OreLabel extends OreWidget {
    private IText text;
    private final TypeRole role;
    private final LabelOverflow overflow;
    private final int maxLines;
    private final TextAlign align;
    private List<String> lines = List.of();

    public OreLabel(ComponentContext context, IText text, TypeRole role, LabelOverflow overflow,
                    int maxWidth) {
        this(context, text, role, overflow, maxWidth, 3, TextAlign.START);
    }

    public OreLabel(ComponentContext context, IText text, TypeRole role, LabelOverflow overflow,
                    int maxWidth, int maxLines, TextAlign align) {
        super(context);
        this.text = text;
        this.role = role;
        this.overflow = overflow;
        this.maxLines = maxLines;
        this.align = align;
        style().width(maxWidth).height(context.theme().type(role).lineHeight());
        refresh();
    }

    public void setText(IText text) {
        this.text = text;
        refresh();
    }

    public List<String> lines() {
        return lines;
    }

    private void refresh() {
        float width = style().width();
        float maxWidth = Float.isNaN(width) ? 200f : width;
        TextMetrics metrics = context.textMeasurer().measure(text, role, maxWidth);
        List<String> measured = metrics.lines();
        List<String> next = new ArrayList<>();
        if (measured.isEmpty()) {
            next.add("");
        } else if (overflow == LabelOverflow.WRAP) {
            for (int i = 0; i < Math.min(maxLines, measured.size()); i++) {
                next.add(measured.get(i));
            }
        } else {
            String first = measured.get(0);
            if (overflow == LabelOverflow.ELLIPSIS && measured.size() > 1) {
                first = first + "…";
            }
            next.add(first);
        }
        lines = List.copyOf(next);
        style().height(lines.size() * context.theme().type(role).lineHeight());
        markDirty();
    }

    @Override
    public void renderForeground(dev.oreui.components.platform.IRenderContext ctx) {
        ColorRole color = context.theme().type(role).color();
        float lineHeight = context.theme().type(role).lineHeight();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            float width = context.textMeasurer()
                    .measure(new LiteralText(line), role, 0f).width();
            float x = bounds().x();
            if (align == TextAlign.CENTER) {
                x += (bounds().width() - width) * 0.5f;
            } else if (align == TextAlign.END) {
                x += bounds().width() - width;
            }
            text(ctx, new LiteralText(line), x, bounds().y() + i * lineHeight, role, color);
        }
    }
}