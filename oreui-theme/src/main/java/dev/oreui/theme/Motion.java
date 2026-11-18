package dev.oreui.theme;

public enum Motion {
    INSTANT(0),
    FAST(80),
    BASE(140),
    SLOW(200),
    TOOLTIP_DELAY(400),
    TOAST_DWELL(3500),
    INDETERMINATE_PERIOD(1200),
    CARET_BLINK(500),
    SCROLLBAR_IDLE(600);

    private final int millis;

    Motion(int millis) {
        this.millis = millis;
    }

    public int millis() {
        return millis;
    }
}