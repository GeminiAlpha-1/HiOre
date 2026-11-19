package dev.oreui.adapter262;

/**
 * Runtime OreUI font selection. Switching takes effect on the next frame without reloading
 * resources or restarting the game.
 */
public final class OreFonts {
    public enum Family {
        DEFAULT,
        HARMONY
    }

    private static Family bodyFamily = Family.DEFAULT;
    private static int generation;

    private OreFonts() {
    }

    public static Family bodyFamily() {
        return bodyFamily;
    }

    public static int generation() {
        return generation;
    }

    public static void setBodyFamily(Family family) {
        if (family == null || family == bodyFamily) {
            return;
        }
        bodyFamily = family;
        generation++;
        MinecraftFonts.invalidateBody();
    }
}