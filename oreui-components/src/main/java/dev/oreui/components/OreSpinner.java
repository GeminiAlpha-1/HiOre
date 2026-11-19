package dev.oreui.components;

import dev.oreui.components.platform.IRenderContext;
import dev.oreui.theme.ColorRole;
import dev.oreui.theme.Motion;

public final class OreSpinner extends OreWidget {
    private static final int FRAME_COUNT = 16;
    private static final int FRAME_SIZE = 8;
    private static final int[] RING_X = {
            4, 5, 6, 7,
            7, 7, 7, 7, 7, 7, 7,
            6, 5, 4, 3, 2, 1, 0,
            0, 0, 0, 0, 0, 0,
            0, 1, 2, 3,
    };
    private static final int[] RING_Y = {
            0, 0, 0, 0,
            1, 2, 3, 4, 5, 6, 7,
            7, 7, 7, 7, 7, 7, 7,
            6, 5, 4, 3, 2, 1,
            0, 0, 0, 0,
    };

    private float elapsed;

    public OreSpinner(ComponentContext context) {
        super(context);
        style().width(16f).height(16f);
    }

    @Override
    public void update(float deltaSeconds) {
        float period = Motion.SPINNER_PERIOD.millis() / 1000f;
        elapsed = (elapsed + deltaSeconds) % period;
    }

    @Override
    public void renderBackground(IRenderContext ctx) {
        float period = Motion.SPINNER_PERIOD.millis() / 1000f;
        int frame = Math.min(FRAME_COUNT - 1, (int) (elapsed / period * FRAME_COUNT));
        int cell = Math.max(1, (int) (Math.min(bounds().width(), bounds().height()) / FRAME_SIZE));
        float size = (float) cell * FRAME_SIZE;
        float originX = bounds().x() + (bounds().width() - size) * 0.5f;
        float originY = bounds().y() + (bounds().height() - size) * 0.5f;
        fill(ctx, ColorRole.ICON_MUTED, originX, originY, size, cell);
        fill(ctx, ColorRole.ICON_MUTED, originX, originY + size - cell, size, cell);
        fill(ctx, ColorRole.ICON_MUTED, originX, originY + cell, cell, size - cell * 2f);
        fill(ctx, ColorRole.ICON_MUTED, originX + size - cell, originY + cell,
                cell, size - cell * 2f);
        int span = Math.max(1, Math.round(RING_X.length / 3f));
        int start = frame * RING_X.length / FRAME_COUNT;
        for (int i = 0; i < span; i++) {
            int index = (start + i) % RING_X.length;
            fill(ctx, ColorRole.ICON_PRIMARY, originX + RING_X[index] * cell,
                    originY + RING_Y[index] * cell, cell, cell);
        }
    }
}