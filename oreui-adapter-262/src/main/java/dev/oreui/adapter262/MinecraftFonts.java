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
    private static boolean resolved;

    private MinecraftFonts() {
    }

    static Font display() {
        if (!resolved) {
            resolved = true;
            display = resolveDisplay();
        }
        return display == null ? Minecraft.getInstance().font : display;
    }

    @SuppressWarnings("unchecked")
    private static Font resolveDisplay() {
        try {
            Minecraft minecraft = Minecraft.getInstance();
            Field managerField = Minecraft.class.getDeclaredField("fontManager");
            managerField.setAccessible(true);
            FontManager manager = (FontManager) managerField.get(minecraft);
            Field setsField = FontManager.class.getDeclaredField("fontSets");
            setsField.setAccessible(true);
            Map<Identifier, FontSet> sets = (Map<Identifier, FontSet>) setsField.get(manager);
            FontSet set = sets.get(Identifier.fromNamespaceAndPath("oreui", "minecraft_ten"));
            if (set == null) {
                LOG.warn("[oreui] font set oreui:minecraft_ten not found; falling back to default");
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
            LOG.info("[oreui] Minecraft Ten display font attached");
            return font;
        } catch (ReflectiveOperationException | RuntimeException exception) {
            LOG.warn("[oreui] Minecraft Ten unavailable: {}", exception.toString());
            return null;
        }
    }
}