package dev.oreui.components;

import dev.oreui.components.platform.IRenderContext;
import dev.oreui.components.platform.IText;
import dev.oreui.components.platform.TextMetrics;
import dev.oreui.core.text.TextGeometry;
import dev.oreui.core.text.TextMeasure;
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
    private int inkTop;
    private int inkHeight;

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
        inkTop = metrics.inkTop();
        inkHeight = metrics.inkHeight();
        List<String> measured = metrics.lines();
        List<String> next = new ArrayList<>();
        if (measured.isEmpty()) {
            next.add("");
        } else if (overflow == LabelOverflow.WRAP) {
            for (int i = 0; i < Math.min(maxLines, measured.size()); i++) {
                next.add(measured.get(i));
            }
        } else if (overflow == LabelOverflow.CLIP) {
            String clipped = TextGeometry.ellipsize(context.textSource().resolve(text), maxWidth, measure());
            if (clipped.endsWith(TextGeometry.ELLIPSIS)) {
                clipped = clipped.substring(0, clipped.length() - TextGeometry.ELLIPSIS.length());
            }
            next.add(clipped);
        } else {
            next.add(TextGeometry.ellipsize(context.textSource().resolve(text), maxWidth, measure()));
        }
        lines = List.copyOf(next);
        style().height(lines.size() * context.theme().type(role).lineHeight());
        markDirty();
    }

    private TextMeasure measure() {
        return value -> context.textMeasurer()
                .measure(new LiteralText(value.toString()), role, 0f).width();
    }

    @Override
    public void renderForeground(IRenderContext ctx) {
        ColorRole color = context.theme().type(role).color();
        float lineHeight = context.theme().type(role).lineHeight();
        float inkOffset = inkHeight > 0
                ? Math.round((lineHeight - inkHeight) * 0.5f) - inkTop : 0f;
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
            textClipped(ctx, new LiteralText(line), x,
                    bounds().y() + i * lineHeight + inkOffset, role, color, bounds());
        }
    }
}