package dev.oreui.components;

import dev.oreui.components.platform.IRenderContext;
import dev.oreui.theme.ColorRole;

public final class OreDivider extends OreWidget {
    private final Orientation orientation;

    public OreDivider(ComponentContext context, Orientation orientation) {
        super(context);
        this.orientation = orientation;
        if (orientation == Orientation.HORIZONTAL) {
            style().height(2f);
        } else {
            style().width(2f);
        }
    }

    @Override
    public void renderBackground(IRenderContext ctx) {
        float x = bounds().x();
        float y = bounds().y();
        if (orientation == Orientation.HORIZONTAL) {
            fill(ctx, ColorRole.DIVIDER_DARK, x, y, bounds().width(), 1f);
            fill(ctx, ColorRole.DIVIDER_LIGHT, x, y + 1f, bounds().width(), 1f);
        } else {
            fill(ctx, ColorRole.DIVIDER_DARK, x, y, 1f, bounds().height());
            fill(ctx, ColorRole.DIVIDER_LIGHT, x + 1f, y, 1f, bounds().height());
        }
    }
}