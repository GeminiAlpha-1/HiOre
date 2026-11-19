package dev.oreui.components;

import dev.oreui.components.platform.IRenderContext;
import dev.oreui.core.geom.Insets;
import dev.oreui.layout.LayoutStyle;
import dev.oreui.theme.ColorRole;

public final class OreSurface extends OreWidget {
    private final SurfaceVariant variant;

    public OreSurface(ComponentContext context, SurfaceVariant variant) {
        super(context);
        this.variant = variant;
        style().direction(LayoutStyle.Direction.COLUMN);
        style().padding(new Insets(12f, 12f, 12f, 12f));
        style().gap(6f);
    }

    @Override
    public void renderBackground(IRenderContext ctx) {
        float x = bounds().x();
        float y = bounds().y();
        float w = bounds().width();
        float h = bounds().height();
        switch (variant) {
            case PANEL, CARD, TOOLBAR -> {
                fill(ctx, variant == SurfaceVariant.PANEL ? ColorRole.SURFACE_BASE : ColorRole.SURFACE_CARD,
                        x, y, w, h);
                outline(ctx, ColorRole.BORDER_OUTER, x, y, w, h, 1f);
                fill(ctx, ColorRole.BEVEL_LIGHT, x + 1f, y + 1f, w - 2f, 1f);
                fill(ctx, ColorRole.BEVEL_LIGHT, x + 1f, y + 1f, 1f, h - 2f);
                fill(ctx, ColorRole.BEVEL_DARK, x + 1f, y + h - 2f, w - 2f, 1f);
                fill(ctx, ColorRole.BEVEL_DARK, x + w - 2f, y + 1f, 1f, h - 2f);
            }
            case WELL -> {
                fill(ctx, ColorRole.SURFACE_SUNKEN, x, y, w, h);
                fill(ctx, ColorRole.WELL_SHADOW, x, y, w, 2f);
                fill(ctx, ColorRole.WELL_RIM, x, y + h - 1f, w, 1f);
                outline(ctx, ColorRole.BORDER_OUTER, x, y, w, h, 1f);
            }
        }
        super.renderBackground(ctx);
    }

}