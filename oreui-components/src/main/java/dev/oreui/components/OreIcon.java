package dev.oreui.components;

import dev.oreui.components.platform.IRenderContext;
import dev.oreui.theme.ColorRole;

public final class OreIcon extends OreWidget {
    private final IconKind kind;
    private final int size;

    public OreIcon(ComponentContext context, IconKind kind, int size) {
        super(context);
        this.kind = kind;
        this.size = size;
        style().width(size).height(size);
    }

    @Override
    public void renderBackground(IRenderContext ctx) {
        float x = bounds().x();
        float y = bounds().y();
        float k = size / 12f;
        float px = Math.max(1f, Math.round(2f * k));
        ColorRole role = ColorRole.ICON_PRIMARY;
        switch (kind) {
            case CHECK -> {
                fill(ctx, role, x + 2f * k, y + 6f * k, px, px);
                fill(ctx, role, x + 4f * k, y + 8f * k, px, px);
                fill(ctx, role, x + 6f * k, y + 6f * k, px, px);
                fill(ctx, role, x + 8f * k, y + 4f * k, px, px);
            }
            case CHEVRON_DOWN -> {
                fill(ctx, role, x + 2f * k, y + 4f * k, px, px);
                fill(ctx, role, x + 4f * k, y + 6f * k, px, px);
                fill(ctx, role, x + 6f * k, y + 6f * k, px, px);
                fill(ctx, role, x + 8f * k, y + 4f * k, px, px);
            }
            case CLOSE -> {
                fill(ctx, role, x + 2f * k, y + 2f * k, px, px);
                fill(ctx, role, x + 4f * k, y + 4f * k, px, px);
                fill(ctx, role, x + 6f * k, y + 6f * k, px, px);
                fill(ctx, role, x + 8f * k, y + 8f * k, px, px);
                fill(ctx, role, x + 8f * k, y + 2f * k, px, px);
                fill(ctx, role, x + 6f * k, y + 4f * k, px, px);
                fill(ctx, role, x + 4f * k, y + 6f * k, px, px);
                fill(ctx, role, x + 2f * k, y + 8f * k, px, px);
            }
            case SEARCH -> {
                float stroke = Math.max(1f, k);
                fill(ctx, role, x + 2f * k, y + 2f * k, 6f * k, stroke);
                fill(ctx, role, x + 2f * k, y + 2f * k, stroke, 6f * k);
                fill(ctx, role, x + 7f * k, y + 2f * k, stroke, 6f * k);
                fill(ctx, role, x + 2f * k, y + 7f * k, 6f * k, stroke);
                fill(ctx, role, x + 8f * k, y + 8f * k, Math.max(2f, 3f * k), Math.max(1f, 2f * k));
            }
            case DOT -> fill(ctx, role, x + 4f * k, y + 4f * k,
                    Math.max(2f, 4f * k), Math.max(2f, 4f * k));
        }
    }
}