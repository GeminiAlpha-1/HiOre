package dev.oreui.components;

import dev.oreui.components.platform.IRenderContext;
import dev.oreui.theme.ColorRole;

public final class OreDivider extends OreWidget {
    private final Orientation orientation;

    public OreDivider(ComponentContext context, Orientation orientation) {
        super(context);
        this.orientation = orientation;
        if (orientation == Orientation.HORIZONTAL) {
            style().height(1f);
        } else {
            style().width(1f);
        }
    }

    @Override
    public void renderBackground(IRenderContext ctx) {
        if (orientation == Orientation.HORIZONTAL) {
            fill(ctx, ColorRole.DIVIDER, bounds().x(), bounds().y(), bounds().width(), 1f);
        } else {
            fill(ctx, ColorRole.DIVIDER, bounds().x(), bounds().y(), 1f, bounds().height());
        }
    }
}