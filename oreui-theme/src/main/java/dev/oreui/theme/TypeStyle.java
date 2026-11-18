package dev.oreui.theme;

public record TypeStyle(int fontSize, int lineHeight, ColorRole color) {
    public TypeStyle {
        if (fontSize <= 0 || lineHeight <= 0) {
            throw new IllegalArgumentException("fontSize and lineHeight must be positive");
        }
        if (color == null) {
            throw new NullPointerException("color");
        }
    }
}