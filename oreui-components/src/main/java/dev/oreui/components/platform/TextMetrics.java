package dev.oreui.components.platform;

import java.util.List;

public record TextMetrics(int width, int height, int glyphHeight, List<String> lines) {
    public TextMetrics {
        if (width < 0 || height < 0 || glyphHeight < 0) {
            throw new IllegalArgumentException("width, height and glyphHeight must be non-negative");
        }
        if (lines == null) {
            throw new NullPointerException("lines");
        }
        lines = List.copyOf(lines);
    }

    public TextMetrics(int width, int height, List<String> lines) {
        this(width, height, height, lines);
    }
}