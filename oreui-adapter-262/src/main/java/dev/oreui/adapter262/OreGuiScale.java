package dev.oreui.adapter262;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * OreUI's own GUI scale. By default it follows the vanilla GUI scale; an explicit override lets
 * the library render at a different scale without touching the vanilla options.
 */
public final class OreGuiScale {
    public static final int SYNC = 0;
    private static final int FALLBACK_MAX = 5;
    private static final Logger LOG = LoggerFactory.getLogger("oreui-adapter");

    private static int override;
    private static int maxCap = -1;

    private OreGuiScale() {
    }

    public static int override() {
        return override;
    }

    public static void setOverride(int scale) {
        override = Math.max(0, scale);
    }

    public static int minecraftScale() {
        return Math.max(1, Minecraft.getInstance().getWindow().getGuiScale());
    }

    public static int scale() {
        int mc = minecraftScale();
        return override > 0 ? override : mc;
    }

    public static int width() {
        Window window = Minecraft.getInstance().getWindow();
        return Math.max(1, Math.round(window.getGuiScaledWidth() * (float) minecraftScale() / scale()));
    }

    public static int height() {
        Window window = Minecraft.getInstance().getWindow();
        return Math.max(1, Math.round(window.getGuiScaledHeight() * (float) minecraftScale() / scale()));
    }

    public static float renderScale() {
        return scale() / (float) minecraftScale();
    }

    public static float toOreX(float guiX) {
        return guiX * minecraftScale() / (float) scale();
    }

    public static float toOreY(float guiY) {
        return guiY * minecraftScale() / (float) scale();
    }

    public static int maxScale() {
        Window window = Minecraft.getInstance().getWindow();
        int fit = window.calculateScale(Integer.MAX_VALUE, false);
        return Math.max(1, Math.min(maxCap(), fit));
    }

    public static int[] availableScales() {
        int max = maxScale();
        int[] scales = new int[max];
        for (int i = 0; i < max; i++) {
            scales[i] = i + 1;
        }
        return scales;
    }

    private static int maxCap() {
        if (maxCap > 0) {
            return maxCap;
        }
        try {
            Field field = Options.class.getDeclaredField("MAX_GUI_SCALE_INCLUSIVE");
            field.setAccessible(true);
            maxCap = Math.max(1, field.getInt(null));
        } catch (ReflectiveOperationException | RuntimeException exception) {
            LOG.warn("[oreui] vanilla max GUI scale unavailable: {}", exception.toString());
            maxCap = FALLBACK_MAX;
        }
        return maxCap;
    }
}