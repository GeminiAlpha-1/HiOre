package dev.oreui.components;

import dev.oreui.components.platform.IRenderContext;
import dev.oreui.theme.ColorRole;
import dev.oreui.theme.Motion;

public final class OreSpinner extends OreWidget {
    private static final int FRAME_COUNT = 15;
    private static final int FRAME_SIZE = 8;
    private static final int[] FRAMES = {
            0b00011000, 0b00010000, 0b00010000, 0b00010000, 0b00010000, 0b00010000, 0b00011000, 0b00000000,
            0b00000100, 0b00000100, 0b00000100, 0b00000100, 0b00000100, 0b00000100, 0b00000000, 0b00111000,
            0b00000100, 0b00000100, 0b00000100, 0b00000100, 0b00000100, 0b01000000, 0b01110000, 0b00000000,
            0b00000000, 0b00000001, 0b00000001, 0b10000001, 0b10000000, 0b11110000, 0b00000000, 0b00000000,
            0b00000000, 0b00000000, 0b10000001, 0b10000001, 0b10000000, 0b11110000, 0b00000000, 0b00000000,
            0b00000000, 0b01000000, 0b01000000, 0b01000010, 0b10000000, 0b11110000, 0b00000000, 0b00000000,
            0b00000000, 0b11000000, 0b11000000, 0b11000000, 0b11000000, 0b10000010, 0b11100000, 0b00000000,
            0b00000100, 0b00110000, 0b00110000, 0b00110000, 0b00110000, 0b00100000, 0b00111000, 0b00000000,
            0b00000110, 0b01100000, 0b01100000, 0b01100000, 0b01100000, 0b01000000, 0b01100000, 0b00000000,
            0b00000111, 0b11000000, 0b11000000, 0b11000000, 0b11000000, 0b10000000, 0b10000000, 0b00000000,
            0b00000000, 0b00000111, 0b11001000, 0b11000000, 0b11000000, 0b11000000, 0b00000000, 0b00000000,
            0b00000000, 0b00000111, 0b11001000, 0b11001000, 0b11000000, 0b11000000, 0b00000000, 0b00000000,
            0b00000000, 0b00000000, 0b00000111, 0b11001000, 0b11001000, 0b11001000, 0b00000000, 0b00000000,
            0b00000000, 0b00000111, 0b11001000, 0b11001000, 0b00001000, 0b00001000, 0b00000000, 0b00000000,
            0b00000000, 0b00001110, 0b10010000, 0b00010000, 0b00010000, 0b00010000, 0b00010000, 0b00000000,
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
        int frame = (int) (elapsed / period * FRAME_COUNT) % FRAME_COUNT;
        int cell = Math.max(1, (int) (Math.min(bounds().width(), bounds().height()) / FRAME_SIZE));
        float size = (float) cell * FRAME_SIZE;
        float originX = bounds().x() + (bounds().width() - size) * 0.5f;
        float originY = bounds().y() + (bounds().height() - size) * 0.5f;
        int base = frame * FRAME_SIZE;
        for (int row = 0; row < FRAME_SIZE; row++) {
            int bits = FRAMES[base + row];
            for (int col = 0; col < FRAME_SIZE; col++) {
                if ((bits & (1 << (FRAME_SIZE - 1 - col))) != 0) {
                    fill(ctx, ColorRole.ICON_PRIMARY, originX + col * cell,
                            originY + row * cell, cell, cell);
                }
            }
        }
    }
}