package dev.oreui.components;

import dev.oreui.components.platform.IRenderContext;
import dev.oreui.components.platform.IText;
import dev.oreui.core.geom.Rect2f;
import dev.oreui.layout.ViewportClamp;
import dev.oreui.theme.ColorRole;
import dev.oreui.theme.TypeRole;

public final class OreTooltip extends OreWidget {
    private final IText text;
    private final TooltipPlacement placement;
    private final Rect2f popup = new Rect2f();
    private boolean shown = true;

    public OreTooltip(ComponentContext context, IText text, TooltipPlacement placement) {
        super(context);
        this.text = text;
        this.placement = placement;
        style().width(180f).height(26f);
    }

    public void setShown(boolean shown) {
        this.shown = shown;
    }

    public void place(Rect2f anchor, Rect2f viewport) {
        float x;
        float y;
        switch (placement) {
            case TOP -> {
                x = anchor.x();
                y = anchor.y() - bounds().height() - 4f;
            }
            case RIGHT -> {
                x = anchor.right() + 4f;
                y = anchor.y();
            }
            case AUTO_CURSOR -> {
                x = anchor.x() + 8f;
                y = anchor.y() + 8f;
            }
            default -> {
                x = anchor.x();
                y = anchor.bottom() + 4f;
            }
        }
        popup.set(x, y, bounds().width(), bounds().height());
        ViewportClamp.clampPopup(popup, viewport);
    }

    public Rect2f popup() {
        return popup;
    }

    private Rect2f area() {
        return popup.width() > 0f ? popup : bounds();
    }

    @Override
    public void renderBackground(IRenderContext ctx) {
        if (!shown) {
            return;
        }
        Rect2f area = area();
        fill(ctx, ColorRole.TOOLTIP_BG, area.x(), area.y(), area.width(), area.height());
        outline(ctx, ColorRole.BORDER_OUTER, area.x(), area.y(), area.width(), area.height(), 1f);
        outline(ctx, ColorRole.TOOLTIP_BORDER, area.x() + 1f, area.y() + 1f,
                area.width() - 2f, area.height() - 2f, 1f);
    }

    @Override
    public void renderForeground(IRenderContext ctx) {
        if (!shown) {
            return;
        }
        Rect2f area = area();
        text(ctx, text, area.x() + 6f, area.y() + 7f, TypeRole.CAPTION, ColorRole.TEXT_PRIMARY);
    }

    public TooltipPlacement placement() {
        return placement;
    }
}