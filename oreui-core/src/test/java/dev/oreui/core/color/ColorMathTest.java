package dev.oreui.core.color;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ColorMathTest {
    @Test
    void lerpHitsEndpointsAndMidpoint() {
        assertEquals(0xFF000000, ColorMath.lerp(0xFF000000, 0xFFFFFFFF, 0f));
        assertEquals(0xFFFFFFFF, ColorMath.lerp(0xFF000000, 0xFFFFFFFF, 1f));
        assertEquals(0xFF808080, ColorMath.lerp(0xFF000000, 0xFFFFFFFF, 0.5f));
    }

    @Test
    void lerpClampsOutsideUnitRange() {
        assertEquals(0xFF000000, ColorMath.lerp(0xFF000000, 0xFFFFFFFF, -1f));
        assertEquals(0xFFFFFFFF, ColorMath.lerp(0xFF000000, 0xFFFFFFFF, 2f));
    }

    @Test
    void lerpIncludesAlphaAndChannels() {
        assertEquals(0x00800080, ColorMath.lerp(0x00FF0000, 0x000000FF, 0.5f));
        assertEquals(0x80FF0000, ColorMath.lerp(0x00FF0000, 0xFFFF0000, 0.5f));
    }

    @Test
    void withAlphaClampsAndPreservesRgb() {
        assertEquals(0x00123456, ColorMath.withAlpha(0xFF123456, 0));
        assertEquals(0xFF123456, ColorMath.withAlpha(0xFF123456, 300));
        assertEquals(0x00123456, ColorMath.withAlpha(0xFF123456, -10));
    }

    @Test
    void channelAccessorsMatchArgb() {
        int color = 0x80112233;
        assertEquals(128, ColorMath.alpha(color));
        assertEquals(17, ColorMath.red(color));
        assertEquals(34, ColorMath.green(color));
        assertEquals(51, ColorMath.blue(color));
    }

    @Test
    void unpackWritesNormalizedChannels() {
        float[] out = new float[4];
        ColorMath.unpack(0x80112233, out);
        assertEquals(128f / 255f, out[0], 0.0001f);
        assertEquals(17f / 255f, out[1], 0.0001f);
        assertEquals(34f / 255f, out[2], 0.0001f);
        assertEquals(51f / 255f, out[3], 0.0001f);
    }

    @Test
    void unpackRejectsShortOrNullBuffers() {
        assertThrows(IllegalArgumentException.class, () -> ColorMath.unpack(0, new float[3]));
        assertThrows(IllegalArgumentException.class, () -> ColorMath.unpack(0, null));
    }
}