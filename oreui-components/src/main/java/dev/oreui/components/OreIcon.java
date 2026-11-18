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
        ColorRole role = ColorRole.ICON_PRIMARY;
        switch (kind) {
            case CHECK -> {
                fill(ctx, role, x + size * 0.2f, y + size * 0.5f, 2f, 2f);
                fill(ctx, role, x + size * 0.4f, y + size * 0.7f, 2f, 2f);
                fill(ctx, role, x + size * 0.6f, y + size * 0.4f, 2f, 2f);
            }
            case CHEVRON_DOWN -> {
                fill(ctx, role, x + size * 0.25f, y + size * 0.4f, 2f, 2f);
                fill(ctx, role, x + size * 0.45f, y + size * 0.6f, 2f, 2f);
                fill(ctx, role, x + size * 0.65f, y + size * 0.4f, 2f, 2f);
            }
            case CLOSE -> {
                fill(ctx, role, x + size * 0.25f, y + size * 0.25f, 2f, 2f);
                fill(ctx, role, x + size * 0.5f, y + size * 0.45f, 2f, 2f);
                fill(ctx, role, x + size * 0.7f, y + size * 0.7f, 2f, 2f);
                fill(ctx, role, x + size * 0.7f, y + size * 0.25f, 2f, 2f);
                fill(ctx, role, x + size * 0.25f, y + size * 0.7f, 2f, 2f);
            }
            case SEARCH -> {
                fill(ctx, role, x + size * 0.2f, y + size * 0.2f, size * 0.5f, 2f);
                fill(ctx, role, x + size * 0.2f, y + size * 0.2f, 2f, size * 0.5f);
                fill(ctx, role, x + size * 0.65f, y + size * 0.2f, 2f, size * 0.5f);
                fill(ctx, role, x + size * 0.7f, y + size * 0.7f, 3f, 2f);
            }
            case DOT -> fill(ctx, role, x + size * 0.35f, y + size * 0.35f, size * 0.3f, size * 0.3f);
        }
    }
}