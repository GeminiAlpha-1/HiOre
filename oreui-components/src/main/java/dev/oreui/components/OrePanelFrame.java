package dev.oreui.components;

import dev.oreui.components.platform.IRenderContext;
import dev.oreui.components.platform.IText;
import dev.oreui.components.platform.TextMetrics;
import dev.oreui.theme.ColorRole;
import dev.oreui.theme.TypeRole;

public final class OrePanelFrame extends OreWidget {
    private static final float TITLE_HEIGHT = 22f;
    private static final float CLOSE_SIZE = 12f;
    private static final float BORDER = 1f;

    private IText title;
    private OreWidget content;
    private Runnable onClose = () -> {
    };
    private boolean dragging;
    private float dragX;
    private float dragY;

    public OrePanelFrame(ComponentContext context, IText title) {
        super(context);
        this.title = title;
        style().width(260f).height(160f);
    }

    public IText title() {
        return title;
    }

    public void setTitle(IText title) {
        this.title = title;
    }

    public void setOnClose(Runnable onClose) {
        this.onClose = onClose == null ? () -> {
        } : onClose;
    }

    public void setContent(OreWidget content) {
        if (this.content != null) {
            removeChild(this.content);
        }
        this.content = content;
        if (content != null) {
            addChild(content);
        }
    }

    public void open() {
        if (overlayManager() != null) {
            overlayManager().push(this);
        }
    }

    public void close() {
        if (overlayManager() != null) {
            overlayManager().remove(this);
        }
        onClose.run();
    }

    public boolean isDragging() {
        return dragging;
    }

    @Override
    public boolean pointerDown(float x, float y) {
        if (!contains(x, y)) {
            return false;
        }
        if (inCloseButton(x, y)) {
            close();
            return true;
        }
        if (y < bounds().y() + TITLE_HEIGHT) {
            dragging = true;
            dragX = x - bounds().x();
            dragY = y - bounds().y();
            return true;
        }
        return true;
    }

    @Override
    public boolean pointerMove(float x, float y) {
        if (!dragging) {
            return false;
        }
        layoutNode().frame().set(x - dragX, y - dragY, bounds().width(), bounds().height());
        syncFrames();
        return true;
    }

    @Override
    public boolean pointerUp(float x, float y) {
        if (!dragging) {
            return false;
        }
        dragging = false;
        return true;
    }

    @Override
    public void syncFrames() {
        super.syncFrames();
        if (content != null) {
            content.layoutNode().frame().set(bounds().x() + BORDER, bounds().y() + TITLE_HEIGHT,
                    bounds().width() - BORDER * 2f, bounds().height() - TITLE_HEIGHT - BORDER);
            content.syncFrames();
        }
    }

    @Override
    public void renderBackground(IRenderContext ctx) {
        float x = bounds().x();
        float y = bounds().y();
        float w = bounds().width();
        float h = bounds().height();
        fill(ctx, ColorRole.SURFACE_BASE, x, y, w, h);
        outline(ctx, ColorRole.BORDER_OUTER, x, y, w, h, BORDER);
        fill(ctx, ColorRole.SURFACE_CARD, x + BORDER, y + BORDER, w - BORDER * 2f, TITLE_HEIGHT - BORDER);
        float closeX = x + w - CLOSE_SIZE - 4f;
        float closeY = y + (TITLE_HEIGHT - CLOSE_SIZE) * 0.5f;
        fill(ctx, ColorRole.ICON_MUTED, closeX + 3f, closeY + 3f, CLOSE_SIZE - 6f, 2f);
        super.renderBackground(ctx);
    }

    @Override
    public void renderForeground(IRenderContext ctx) {
        if (title != null) {
            TextMetrics metrics = context.textMeasurer().measure(title, TypeRole.LABEL, 0f);
            text(ctx, title, bounds().x() + 10f,
                    textCenterY(bounds().y() + 1f, Math.max(1f, TITLE_HEIGHT - 2f), metrics),
                    TypeRole.LABEL, ColorRole.TEXT_PRIMARY);
        }
        super.renderForeground(ctx);
    }

    private boolean inCloseButton(float x, float y) {
        float closeX = bounds().right() - CLOSE_SIZE - 4f;
        float closeY = bounds().y() + (TITLE_HEIGHT - CLOSE_SIZE) * 0.5f;
        return x >= closeX && x < closeX + CLOSE_SIZE && y >= closeY && y < closeY + CLOSE_SIZE;
    }
}