package dev.oreui.core.geom;

public final class Snap {
    private static final float EPSILON = 0.001f;

    private Snap() {
    }

    public static float edge(float value, float guiScale) {
        if (!(guiScale > 0f)) {
            return value;
        }
        return (float) Math.floor(value * guiScale) / guiScale;
    }

    public static boolean near(float value, float guiScale) {
        if (!(guiScale > 0f)) {
            return false;
        }
        return Math.abs(value - edge(value, guiScale)) < EPSILON;
    }
}