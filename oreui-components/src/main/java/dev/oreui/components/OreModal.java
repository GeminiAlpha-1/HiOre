package dev.oreui.components;

import dev.oreui.components.platform.IRenderContext;
import dev.oreui.components.platform.IText;
import dev.oreui.core.geom.Rect2f;
import dev.oreui.theme.ColorRole;
import dev.oreui.theme.TypeRole;

public final class OreModal extends OreWidget {
    private final IText title;
    private final IText body;
    private final ModalSize modalSize;
    private final Rect2f viewport = new Rect2f();
    private boolean open;
    private boolean focusLocked;

    public OreModal(ComponentContext context, IText title, IText body, ModalSize modalSize) {
        super(context);
        this.title = title;
        this.body = body;
        this.modalSize = modalSize;
        style().width(220f).height(modalSize == ModalSize.DIALOG_MD ? 140f : 100f);
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
        focusLocked = open;
    }

    public boolean isFocusLocked() {
        return focusLocked;
    }

    public void setViewport(Rect2f viewport) {
        this.viewport.set(viewport);
    }

    private Rect2f area() {
        return viewport.isEmpty() ? bounds() : viewport;
    }

    @Override
    public boolean pointerDown(float x, float y) {
        if (open && area().contains(x, y)) {
            setOpen(false);
            return true;
        }
        return false;
    }

    @Override
    public void renderBackground(IRenderContext ctx) {
        if (!open) {
            return;
        }
        Rect2f area = area();
        fill(ctx, ColorRole.BACKDROP, area.x(), area.y(), area.width(), area.height());
        float w = Math.min(220f, area.width() - 16f);
        float h = Math.min(modalSize == ModalSize.FULLSCREEN ? area.height() - 16f : 100f, area.height() - 16f);
        float x = area.x() + (area.width() - w) * 0.5f;
        float y = area.y() + (area.height() - h) * 0.5f;
        fill(ctx, ColorRole.SURFACE_BASE, x, y, w, h);
        outline(ctx, ColorRole.BORDER_OUTER, x, y, w, h, 1f);
        fill(ctx, ColorRole.BEVEL_LIGHT, x + 1f, y + 1f, w - 2f, 1f);
        fill(ctx, ColorRole.BEVEL_DARK, x + 1f, y + h - 1f, w - 2f, 1f);
        fill(ctx, ColorRole.SURFACE_RAISED_DEFAULT, x + w - 70f, y + h - 26f, 60f, 18f);
        outline(ctx, ColorRole.BORDER_OUTER, x + w - 70f, y + h - 26f, 60f, 18f, 1f);
    }

    @Override
    public void renderForeground(IRenderContext ctx) {
        if (!open) {
            return;
        }
        Rect2f area = area();
        float w = Math.min(220f, area.width() - 16f);
        float h = Math.min(modalSize == ModalSize.FULLSCREEN ? area.height() - 16f : 100f, area.height() - 16f);
        float x = area.x() + (area.width() - w) * 0.5f + 12f;
        float y = area.y() + (area.height() - h) * 0.5f + 12f;
        text(ctx, title, x, y, TypeRole.TITLE, ColorRole.TEXT_PRIMARY);
        text(ctx, body, x, y + 26f, TypeRole.BODY, ColorRole.TEXT_SECONDARY);
        text(ctx, new LiteralText("OK"), x + w - 42f, y + h - 21f, TypeRole.LABEL, ColorRole.TEXT_PRIMARY);
    }

    public ModalSize modalSize() {
        return modalSize;
    }
}