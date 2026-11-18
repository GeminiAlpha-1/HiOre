package dev.oreui.theme;

public enum Space {
    NONE(0),
    XXS(2),
    XS(4),
    SM(8),
    MD(12),
    LG(16),
    XL(24),
    XXL(32);

    private final int value;

    Space(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}