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

import java.util.List;

public final class Fabric26TextMeasurer implements ITextMeasurer {
    private final Font font;
    private final ITextSource textSource;
    private final OreTheme theme;

    public Fabric26TextMeasurer(Font font, ITextSource textSource, OreTheme theme) {
        this.font = font;
        this.textSource = textSource;
        this.theme = theme;
    }

    @Override
    public TextMetrics measure(IText text, TypeRole role, float maxWidth) {
        String resolved = textSource.resolve(text);
        TypeStyle style = theme.type(role);
        int fullWidth = font.width(resolved);
        if (!(maxWidth > 0f) || fullWidth <= maxWidth) {
            return new TextMetrics(fullWidth, style.lineHeight(), font.lineHeight, List.of(resolved));
        }
        TextLayout layout = new TextLayout();
        TextGeometry.wrap(resolved, maxWidth, value -> font.width(value.toString()), 64, layout);
        int widest = 0;
        for (String line : layout.lines()) {
            widest = Math.max(widest, font.width(line));
        }
        return new TextMetrics(widest, layout.size() * style.lineHeight(), font.lineHeight, layout.lines());
    }
}