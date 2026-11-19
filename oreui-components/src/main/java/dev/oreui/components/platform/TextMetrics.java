package dev.oreui.components.platform;

import java.util.List;

/**
 * Immutable measurement of a resolved text block.
 *
 * <p>{@code inkTop} and {@code inkHeight} describe the actual glyph ink box of the first line
 * relative to the top of that line (the coordinate passed to the text renderer). They must be
 * measured from the font's baked glyph bounds, never estimated from line height.
 */
public record TextMetrics(int width, int height, int glyphHeight, int inkTop, int inkHeight,
                          List<String> lines) {
    public TextMetrics {
        if (width < 0 || height < 0 || glyphHeight < 0 || inkHeight < 0) {
            throw new IllegalArgumentException(
                    "width, height, glyphHeight and inkHeight must be non-negative");
        }
        if (lines == null) {
            throw new NullPointerException("lines");
        }
        lines = List.copyOf(lines);
    }
}