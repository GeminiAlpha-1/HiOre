package dev.oreui.components;

import dev.oreui.theme.SizeToken;

public enum ControlSize {
    SM(SizeToken.CONTROL_HEIGHT_SM.value()),
    MD(SizeToken.CONTROL_HEIGHT_MD.value()),
    LG(SizeToken.CONTROL_HEIGHT_LG.value());

    private final int height;

    ControlSize(int height) {
        this.height = height;
    }

    public int height() {
        return height;
    }
}