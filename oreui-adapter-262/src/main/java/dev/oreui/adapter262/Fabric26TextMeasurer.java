package dev.oreui.adapter262;

import dev.oreui.components.platform.IText;
import dev.oreui.components.platform.ITextMeasurer;
import dev.oreui.components.platform.ITextSource;
import dev.oreui.components.platform.TextMetrics;
import dev.oreui.core.text.TextGeometry;
import dev.oreui.core.text.TextLayout;
import dev.oreui.theme.OreTheme;
import dev.oreui.theme.TypeRole;
import dev.oreui.theme.TypeStyle;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.navigation.ScreenRectangle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Fabric26TextMeasurer implements ITextMeasurer {
    private static final int INK_CACHE_LIMIT = 256;

    private final Font font;
    private final ITextSource textSource;
    private final OreTheme theme;
    private final Map<String, Ink> bodyInk = new HashMap<>();
    private final Map<String, Ink> displayInk = new HashMap<>();

    public Fabric26TextMeasurer(Font font, ITextSource textSource, OreTheme theme) {
        this.font = font;
        this.textSource = textSource;
        this.theme = theme;
    }

    @Override
    public TextMetrics measure(IText text, TypeRole role, float maxWidth) {
        String resolved = textSource.resolve(text);
        TypeStyle style = theme.type(role);
        boolean display = role == TypeRole.TITLE;
        Font roleFont = display ? MinecraftFonts.display() : font;
        Ink ink = ink(roleFont, display ? displayInk : bodyInk, resolved);
        int fullWidth = roleFont.width(resolved);
        if (!(maxWidth > 0f) || fullWidth <= maxWidth) {
            return new TextMetrics(fullWidth, style.lineHeight(), roleFont.lineHeight,
                    ink.top(), ink.height(), List.of(resolved));
        }
        TextLayout layout = new TextLayout();
        TextGeometry.wrap(resolved, maxWidth, value -> roleFont.width(value.toString()), 64, layout);
        int widest = 0;
        for (String line : layout.lines()) {
            widest = Math.max(widest, roleFont.width(line));
        }
        Ink firstLine = layout.lines().isEmpty()
                ? ink
                : ink(roleFont, display ? displayInk : bodyInk, layout.lines().get(0));
        return new TextMetrics(widest, layout.size() * style.lineHeight(), roleFont.lineHeight,
                firstLine.top(), firstLine.height(), layout.lines());
    }

    private static Ink ink(Font roleFont, Map<String, Ink> cache, String value) {
        Ink cached = cache.get(value);
        if (cached != null) {
            return cached;
        }
        Ink measured = measureInk(roleFont, value);
        if (cache.size() >= INK_CACHE_LIMIT) {
            cache.clear();
        }
        cache.put(value, measured);
        return measured;
    }

    private static Ink measureInk(Font roleFont, String value) {
        if (value.isEmpty()) {
            return new Ink(0, 0);
        }
        Font.PreparedText prepared = roleFont.prepareText(value, 0f, 0f, 0, false, 0);
        ScreenRectangle bounds = prepared.bounds();
        if (bounds == null || bounds.height() <= 0) {
            return new Ink(0, 0);
        }
        return new Ink(bounds.top(), bounds.height());
    }

    private record Ink(int top, int height) {
    }
}