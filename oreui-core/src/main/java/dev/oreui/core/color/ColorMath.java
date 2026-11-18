package dev.oreui.core.color;

public final class ColorMath {
    private static final int CHANNEL_MASK = 255;
    private static final int ALPHA_SHIFT = 24;
    private static final int RED_SHIFT = 16;
    private static final int GREEN_SHIFT = 8;
    private static final int RGB_MASK = (1 << 24) - 1;
    private static final float CHANNEL_MAX = 255f;

    private ColorMath() {
    }

    public static int lerp(int from, int to, float t) {
        float clamped = clamp01(t);
        int alpha = lerpChannel((from >>> ALPHA_SHIFT) & CHANNEL_MASK, (to >>> ALPHA_SHIFT) & CHANNEL_MASK, clamped);
        int red = lerpChannel((from >>> RED_SHIFT) & CHANNEL_MASK, (to >>> RED_SHIFT) & CHANNEL_MASK, clamped);
        int green = lerpChannel((from >>> GREEN_SHIFT) & CHANNEL_MASK, (to >>> GREEN_SHIFT) & CHANNEL_MASK, clamped);
        int blue = lerpChannel(from & CHANNEL_MASK, to & CHANNEL_MASK, clamped);
        return (alpha << ALPHA_SHIFT) | (red << RED_SHIFT) | (green << GREEN_SHIFT) | blue;
    }

    public static int withAlpha(int color, int alpha) {
        return (clampAlpha(alpha) << ALPHA_SHIFT) | (color & RGB_MASK);
    }

    public static void unpack(int color, float[] out) {
        if (out == null || out.length < 4) {
            throw new IllegalArgumentException("out must have at least four slots");
        }
        out[0] = ((color >>> ALPHA_SHIFT) & CHANNEL_MASK) / CHANNEL_MAX;
        out[1] = ((color >>> RED_SHIFT) & CHANNEL_MASK) / CHANNEL_MAX;
        out[2] = ((color >>> GREEN_SHIFT) & CHANNEL_MASK) / CHANNEL_MAX;
        out[3] = (color & CHANNEL_MASK) / CHANNEL_MAX;
    }

    public static int alpha(int color) {
        return (color >>> ALPHA_SHIFT) & CHANNEL_MASK;
    }

    public static int red(int color) {
        return (color >>> RED_SHIFT) & CHANNEL_MASK;
    }

    public static int green(int color) {
        return (color >>> GREEN_SHIFT) & CHANNEL_MASK;
    }

    public static int blue(int color) {
        return color & CHANNEL_MASK;
    }

    private static float clamp01(float value) {
        if (value < 0f) {
            return 0f;
        }
        return value > 1f ? 1f : value;
    }

    private static int clampAlpha(int value) {
        if (value < 0) {
            return 0;
        }
        return value > CHANNEL_MASK ? CHANNEL_MASK : value;
    }

    private static int lerpChannel(int from, int to, float t) {
        return Math.round(from + (to - from) * t);
    }
}