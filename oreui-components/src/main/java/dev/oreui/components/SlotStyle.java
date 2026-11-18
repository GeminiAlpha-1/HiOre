package dev.oreui.components;

import dev.oreui.theme.SizeToken;

public enum SlotStyle {
    STANDARD_18(SizeToken.SLOT_STANDARD.value(), SizeToken.SLOT_STANDARD.value() - 2),
    LARGE_26(SizeToken.SLOT_LARGE.value(), SizeToken.SLOT_LARGE_INNER.value()),
    OUTPUT_ONLY(SizeToken.SLOT_STANDARD.value(), SizeToken.SLOT_STANDARD.value() - 2),
    GHOST_FILTER(SizeToken.SLOT_STANDARD.value(), SizeToken.SLOT_STANDARD.value() - 2);

    private final int outer;
    private final int inner;

    SlotStyle(int outer, int inner) {
        this.outer = outer;
        this.inner = inner;
    }

    public int outer() {
        return outer;
    }

    public int inner() {
        return inner;
    }
}