package dev.oreui.theme;

public enum Border {
    NONE(0),
    HAIRLINE(1),
    THICK(2),
    LIP_BUTTON(2),
    LIP_PANEL(3);

    private final int value;

    Border(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}