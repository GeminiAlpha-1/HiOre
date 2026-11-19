package dev.oreui.components;

import dev.oreui.components.platform.IRenderContext;
import dev.oreui.theme.ColorRole;
import dev.oreui.theme.Radius;

public final class OreScrollPane extends OreWidget {
    private final OreWidget content;
    private final ScrollBarPolicy policy;
    private float scrollY;

    public OreScrollPane(ComponentContext context, OreWidget content, ScrollBarPolicy policy) {
        super(context);
        this.content = content;
        this.policy = policy;
        style().height(100f);
        addChild(content);
    }

    public OreWidget content() {
        return content;
    }

    public float scrollY() {
        return scrollY;
    }

    public void scrollBy(float delta) {
        float contentHeight = Math.max(content.bounds().height(), content.style().height());
        float max = Math.max(0f, contentHeight - bounds().height());
        scrollY = Math.max(0f, Math.min(max, scrollY + delta));
    }

    @Override
    public boolean wheel(int delta) {
        float before = scrollY;
        scrollBy(delta * 20f);
        return scrollY != before;
    }

    @Override
    public void renderBackground(IRenderContext ctx) {
        fill(ctx, ColorRole.SURFACE_SUNKEN, bounds().x(), bounds().y(), bounds().width(), bounds().height());
        outline(ctx, ColorRole.BORDER_OUTER, bounds().x(), bounds().y(), bounds().width(), bounds().height(), 1f);
        ctx.pushClip(bounds());
        ctx.pushTransform(0f, -scrollY, 1f);
        try {
            content.renderBackground(ctx);
        } finally {
            ctx.popTransform();
            ctx.popClip();
        }
        if (policy != ScrollBarPolicy.HIDDEN) {
            float trackX = bounds().x() + bounds().width() - 4f;
            fill(ctx, ColorRole.SCROLL_TRACK, trackX, bounds().y() + 1f, 4f, bounds().height() - 2f);
            float ratio = content.bounds().height() <= bounds().height() ? 1f
                    : bounds().height() / content.bounds().height();
            float thumbH = Math.max(8f, (bounds().height() - 2f) * ratio);
            float maxScroll = Math.max(1f, content.bounds().height() - bounds().height());
            float thumbY = bounds().y() + 1f + (bounds().height() - 2f - thumbH) * (scrollY / maxScroll);
            fillRounded(ctx, ColorRole.SCROLL_THUMB, trackX, thumbY, 4f, thumbH, Radius.SM.value());
        }
    }

    @Override
    public void renderForeground(IRenderContext ctx) {
        ctx.pushClip(bounds());
        ctx.pushTransform(0f, -scrollY, 1f);
        try {
            content.renderForeground(ctx);
        } finally {
            ctx.popTransform();
            ctx.popClip();
        }
    }
}