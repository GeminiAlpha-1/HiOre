package dev.oreui.components;

import dev.oreui.components.platform.IRenderContext;
import dev.oreui.components.platform.IText;
import dev.oreui.components.platform.TextMetrics;
import dev.oreui.core.geom.Rect2f;
import dev.oreui.layout.LayoutStyle;
import dev.oreui.theme.ColorRole;
import dev.oreui.theme.SizeToken;
import dev.oreui.theme.TypeRole;

import java.util.ArrayList;
import java.util.List;

public final class OreAccordion extends OreWidget {
    private final AccordionMode mode;
    private final List<Section> sections = new ArrayList<>();

    public OreAccordion(ComponentContext context, AccordionMode mode) {
        super(context);
        this.mode = mode;
        style().direction(LayoutStyle.Direction.COLUMN);
        style().gap(0f);
    }

    public int sectionCount() {
        return sections.size();
    }

    public boolean isExpanded(int index) {
        return sections.get(index).expanded;
    }

    public void toggle(int index) {
        setExpanded(index, !sections.get(index).expanded);
    }

    public void setExpanded(int index, boolean expanded) {
        Section target = sections.get(index);
        if (mode == AccordionMode.SINGLE && expanded) {
            for (int i = 0; i < sections.size(); i++) {
                if (i != index) {
                    sections.get(i).expanded = false;
                }
            }
        }
        target.expanded = expanded;
        rebuildChildren();
    }

    public OreAccordion addSection(IText title, OreWidget content) {
        Section section = new Section(title, content);
        Header header = new Header(context, title);
        int index = sections.size();
        header.onClick = () -> toggle(index);
        section.header = header;
        sections.add(section);
        rebuildChildren();
        return this;
    }

    private void rebuildChildren() {
        List<OreWidget> current = new ArrayList<>(children());
        for (OreWidget child : current) {
            removeChild(child);
        }
        for (Section section : sections) {
            section.header.expanded = section.expanded;
            addChild(section.header);
            if (section.expanded) {
                addChild(section.content);
            }
        }
        markDirty();
    }

    @Override
    public void renderBackground(IRenderContext ctx) {
        for (Section section : sections) {
            if (!section.expanded) {
                continue;
            }
            Rect2f area = section.content.bounds();
            if (area.isEmpty()) {
                continue;
            }
            fill(ctx, ColorRole.SURFACE_CARD, area.x(), area.y(), area.width(), area.height());
            outline(ctx, ColorRole.BORDER_OUTER, area.x(), area.y(), area.width(), area.height(), 1f);
        }
        super.renderBackground(ctx);
    }

    private static final class Section {
        private final IText title;
        private final OreWidget content;
        private Header header;
        private boolean expanded;

        private Section(IText title, OreWidget content) {
            this.title = title;
            this.content = content;
        }
    }

    private static final class Header extends OreInteractive {
        private final IText title;
        private Runnable onClick = () -> {
        };
        private boolean expanded;

        private Header(ComponentContext context, IText title) {
            super(context);
            this.title = title;
            style().height(SizeToken.TAB_HEIGHT.value());
            style().width(180f);
        }

        @Override
        protected void activate() {
            onClick.run();
        }

        @Override
        public void renderBackground(IRenderContext ctx) {
            float x = bounds().x();
            float y = bounds().y();
            float w = bounds().width();
            float h = bounds().height();
            fill(ctx, ColorRole.SURFACE_CARD, x, y, w, h);
            fill(ctx, ColorRole.BORDER_OUTER, x, y, w, 1f);
            fill(ctx, ColorRole.BORDER_OUTER, x, y + h - 1f, w, 1f);
            fill(ctx, ColorRole.BORDER_OUTER, x, y, 1f, h);
            fill(ctx, ColorRole.BORDER_OUTER, x + w - 1f, y, 1f, h);
            fill(ctx, ColorRole.BEVEL_LIGHT, x + 1f, y + 1f, w - 2f, 1f);
            drawFocus(ctx);
        }

        @Override
        public void renderForeground(IRenderContext ctx) {
            TextMetrics metrics = context.textMeasurer().measure(title, TypeRole.LABEL, 0f);
            text(ctx, title, bounds().x() + 8f, textCenterY(bounds().y() + 1f, Math.max(1f, bounds().height() - 2f), metrics),
                    TypeRole.LABEL, ColorRole.TEXT_PRIMARY);
            float cx = bounds().right() - 12f;
            float iconTop = bounds().y() + (bounds().height() - 6f) * 0.5f;
            fill(ctx, ColorRole.ICON_MUTED, cx, iconTop + 2f, 6f, 2f);
            if (!expanded) {
                fill(ctx, ColorRole.ICON_MUTED, cx + 2f, iconTop, 2f, 6f);
            }
        }
    }
}