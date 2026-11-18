package dev.oreui.components.platform;

import java.util.List;

public record TextMetrics(int width, int height, List<String> lines) {
    public TextMetrics {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("width and height must be non-negative");
        }
        if (lines == null) {
            throw new NullPointerException("lines");
        }
        lines = List.copyOf(lines);
    }
}