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
    private static Font display;
    private static boolean displayResolved;
    private static Font harmony;
    private static boolean harmonyResolved;

    private MinecraftFonts() {
    }

    static Font display() {
        if (!displayResolved) {
            displayResolved = true;
            display = resolveSet("minecraft_ten");
        }
        return display == null ? Minecraft.getInstance().font : display;
    }

    static Font body() {
        if (OreFonts.bodyFamily() == OreFonts.Family.HARMONY) {
            if (!harmonyResolved) {
                harmonyResolved = true;
                harmony = resolveSet("harmony");
            }
            if (harmony != null) {
                return harmony;
            }
        }
        return Minecraft.getInstance().font;
    }

    static void invalidateBody() {
        harmonyResolved = false;
        harmony = null;
    }

    @SuppressWarnings("unchecked")
    private static Font resolveSet(String name) {
        try {
            Minecraft minecraft = Minecraft.getInstance();
            Field managerField = Minecraft.class.getDeclaredField("fontManager");
            managerField.setAccessible(true);
            FontManager manager = (FontManager) managerField.get(minecraft);
            Field setsField = FontManager.class.getDeclaredField("fontSets");
            setsField.setAccessible(true);
            Map<Identifier, FontSet> sets = (Map<Identifier, FontSet>) setsField.get(manager);
            FontSet set = sets.get(Identifier.fromNamespaceAndPath("oreui", name));
            if (set == null) {
                LOG.warn("[oreui] font set oreui:{} not found; falling back to default", name);
                return null;
            }
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
            LOG.warn("[oreui] font set oreui:{} unavailable: {}", name, exception.toString());
            return null;
        }
    }
}