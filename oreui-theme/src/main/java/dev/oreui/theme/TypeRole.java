package dev.oreui.theme;

public enum TypeRole {
    TITLE(16, 20, ColorRole.TEXT_PRIMARY),
    HEADING(12, 16, ColorRole.TEXT_PRIMARY),
    BODY(9, 12, ColorRole.TEXT_PRIMARY),
    LABEL(8, 10, ColorRole.TEXT_SECONDARY),
    CAPTION(7, 9, ColorRole.TEXT_MUTED),
    MONO(8, 11, ColorRole.TEXT_PRIMARY);

    private final int fontSize;
    private final int lineHeight;
    private final ColorRole color;

    TypeRole(int fontSize, int lineHeight, ColorRole color) {
        this.fontSize = fontSize;
        this.lineHeight = lineHeight;
        this.color = color;
    }

    public int fontSize() {
        return fontSize;
    }

    public int lineHeight() {
        return lineHeight;
    }

    public ColorRole color() {
        return color;
    }

    public TypeStyle toStyle() {
        return new TypeStyle(fontSize, lineHeight, color);
    }
}