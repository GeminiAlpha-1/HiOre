package dev.oreui.components;

import dev.oreui.components.platform.IRenderContext;
import dev.oreui.theme.ColorRole;

public final class OreIcon extends OreWidget {
    private static final int GRID = 8;
    private static final int[] CHECK = {
            0b00000000, 0b00000001, 0b00000010, 0b00000100,
            0b10001000, 0b01010000, 0b00100000, 0b00000000,
    };
    private static final int[] CHEVRON_DOWN = {
            0b00000000, 0b00000000, 0b10000001, 0b01000010,
            0b00100100, 0b00011000, 0b00000000, 0b00000000,
    };
    private static final int[] CLOSE = {
            0b10000001, 0b01000010, 0b00100100, 0b00011000,
            0b00011000, 0b00100100, 0b01000010, 0b10000001,
    };
    private static final int[] SEARCH = {
            0b01110000, 0b10001000, 0b10001000, 0b10001000,
            0b01111000, 0b00000100, 0b00000010, 0b00000001,
    };
    private static final int[] DOT = {
            0b00000000, 0b00000000, 0b00111100, 0b00111100,
            0b00111100, 0b00111100, 0b00000000, 0b00000000,
    };

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
        int cell = Math.max(1, size / GRID);
        float iconSize = (float) cell * GRID;
        float originX = Math.round(bounds().x() + (bounds().width() - iconSize) * 0.5f);
        float originY = Math.round(bounds().y() + (bounds().height() - iconSize) * 0.5f);
        int[] rows = switch (kind) {
            case CHECK -> CHECK;
            case CHEVRON_DOWN -> CHEVRON_DOWN;
            case CLOSE -> CLOSE;
            case SEARCH -> SEARCH;
            case DOT -> DOT;
        };
        ColorRole role = ColorRole.ICON_PRIMARY;
        for (int row = 0; row < GRID; row++) {
            int bits = rows[row];
            for (int col = 0; col < GRID; col++) {
                if ((bits & (1 << (GRID - 1 - col))) != 0) {
                    fill(ctx, role, originX + col * cell, originY + row * cell, cell, cell);
                }
            }
        }
    }
}