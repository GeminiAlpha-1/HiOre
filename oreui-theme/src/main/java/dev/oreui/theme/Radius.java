package dev.oreui.theme;

public enum Radius {
    NONE(0),
    SM(2),
    MD(3),
    LG(4),
    PILL(999);

    private final int value;

    Radius(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}