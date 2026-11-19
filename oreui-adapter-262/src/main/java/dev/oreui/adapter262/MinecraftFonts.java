package dev.oreui.adapter262;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GlyphSource;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.glyphs.EffectGlyph;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Map;

final class MinecraftFonts {
    private static final Logger LOG = LoggerFactory.getLogger("oreui-adapter");

    private static Map<Identifier, FontSet> sets;
    private static Font displayFont;
    private static FontSet displaySet;
    private static Object displayWhite;
    private static Font harmonyFont;
    private static FontSet harmonySet;
    private static Object harmonyWhite;

    private MinecraftFonts() {
    }

    static Font display() {
        Font font = font("minecraft_ten", true);
        return font == null ? Minecraft.getInstance().font : font;
    }

    static Font body() {
        if (OreFonts.bodyFamily() == OreFonts.Family.HARMONY) {
            Font font = font("harmony", false);
            if (font != null) {
                return font;
            }
        }
        return Minecraft.getInstance().font;
    }

    static void invalidateBody() {
        harmonyFont = null;
        harmonySet = null;
        harmonyWhite = null;
    }

    private static Font font(String name, boolean display) {
        FontSet set = fontSet(name);
        if (set == null) {
            LOG.warn("[oreui] font set oreui:{} not found; falling back to default", name);
            return null;
        }
        Object white = set.whiteGlyph();
        if (display) {
            if (displayFont == null || set != displaySet || white != displayWhite) {
                displayFont = createFont(set, name);
                displaySet = set;
                displayWhite = white;
            }
            return displayFont;
        }
        if (harmonyFont == null || set != harmonySet || white != harmonyWhite) {
            harmonyFont = createFont(set, name);
            harmonySet = set;
            harmonyWhite = white;
        }
        return harmonyFont;
    }

    private static FontSet fontSet(String name) {
        Map<Identifier, FontSet> map = fontSets();
        return map == null ? null : map.get(Identifier.fromNamespaceAndPath("oreui", name));
    }

    @SuppressWarnings("unchecked")
    private static Map<Identifier, FontSet> fontSets() {
        if (sets != null) {
            return sets;
        }
        try {
            Minecraft minecraft = Minecraft.getInstance();
            Field managerField = Minecraft.class.getDeclaredField("fontManager");
            managerField.setAccessible(true);
            FontManager manager = (FontManager) managerField.get(minecraft);
            Field setsField = FontManager.class.getDeclaredField("fontSets");
            setsField.setAccessible(true);
            sets = (Map<Identifier, FontSet>) setsField.get(manager);
            return sets;
        } catch (ReflectiveOperationException | RuntimeException exception) {
            LOG.warn("[oreui] font manager unavailable: {}", exception.toString());
            return null;
        }
    }

    private static Font createFont(FontSet set, String name) {
        try {
            Minecraft minecraft = Minecraft.getInstance();
            Field managerField = Minecraft.class.getDeclaredField("fontManager");
            managerField.setAccessible(true);
            FontManager manager = (FontManager) managerField.get(minecraft);
            Font font = manager.createFont();
            Field providerField = Font.class.getDeclaredField("provider");
            providerField.setAccessible(true);
            providerField.set(font, new Font.Provider() {
                @Override
                public GlyphSource glyphs(FontDescription description) {
                    return set.source(true);
                }

                @Override
                public EffectGlyph effect() {
                    return set.whiteGlyph();
                }
            });
            LOG.info("[oreui] font set oreui:{} attached", name);
            return font;
        } catch (ReflectiveOperationException | RuntimeException exception) {
            LOG.warn("[oreui] font creation failed: {}", exception.toString());
            return Minecraft.getInstance().font;
        }
    }
}