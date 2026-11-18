package dev.oreui.components;

import dev.oreui.components.platform.IRenderContext;
import dev.oreui.components.platform.IText;
import dev.oreui.components.platform.TextMetrics;
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
                if (i != index && sections.get(i).expanded) {
                    collapse(sections.get(i));
                }
            }
        }
        if (expanded) {
            expand(target);
        } else {
            collapse(target);
        }
    }

    public OreAccordion addSection(IText title, OreWidget content) {
        Section section = new Section(title, content);
        Header header = new Header(context, title);
        int index = sections.size();
        header.onClick = () -> toggle(index);
        section.header = header;
        sections.add(section);
        addChild(header);
        return this;
    }

    private void expand(Section section) {
        if (section.expanded) {
            return;
        }
        section.expanded = true;
        addChild(section.content);
        markDirty();
    }

    private void collapse(Section section) {
        if (!section.expanded) {
            return;
        }
        section.expanded = false;
        removeChild(section.content);
        markDirty();
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
            fill(ctx, ColorRole.SURFACE_CARD, bounds().x(), bounds().y(), bounds().width(), bounds().height());
            fill(ctx, ColorRole.BORDER_OUTER, bounds().x(), bounds().y() + bounds().height() - 1f,
                    bounds().width(), 1f);
            fill(ctx, ColorRole.BEVEL_LIGHT, bounds().x(), bounds().y(), bounds().width(), 1f);
            drawFocus(ctx);
        }

        @Override
        public void renderForeground(IRenderContext ctx) {
            TextMetrics metrics = context.textMeasurer().measure(title, TypeRole.LABEL, 0f);
            text(ctx, title, bounds().x() + 6f, bounds().y() + (bounds().height() - metrics.height()) * 0.5f,
                    TypeRole.LABEL, ColorRole.TEXT_PRIMARY);
            float cx = bounds().right() - 12f;
            float cy = bounds().y() + bounds().height() * 0.5f - 2f;
            fill(ctx, ColorRole.ICON_MUTED, cx, cy, 6f, 2f);
            fill(ctx, ColorRole.ICON_MUTED, cx + 2f, cy, 2f, 6f);
        }
    }
}