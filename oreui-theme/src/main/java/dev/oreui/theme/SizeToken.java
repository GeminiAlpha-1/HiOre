package dev.oreui.theme;

public enum SizeToken {
    CONTROL_HEIGHT_SM(16),
    CONTROL_HEIGHT_MD(20),
    CONTROL_HEIGHT_LG(24),
    CHOICE_BOX(16),
    SLOT_STANDARD(18),
    SLOT_LARGE(26),
    SLOT_LARGE_INNER(20),
    ICON_SM(8),
    ICON_MD(12),
    ICON_LG(16),
    PROGRESS_HEIGHT_SM(6),
    PROGRESS_HEIGHT_MD(12),
    SLIDER_TRACK(6),
    SLIDER_KNOB_W(8),
    SLIDER_KNOB_H(12),
    SCROLLBAR_W(4),
    SCROLLBAR_MIN_THUMB(8),
    TAB_HEIGHT(22),
    DIVIDER(1),
    BADGE_HEIGHT(12),
    TOOLTIP_MAX_WIDTH(200),
    TOAST_WIDTH(240),
    TOAST_MIN_HEIGHT(36),
    MODAL_W_SM(220),
    MODAL_W_MD(320),
    PANEL_TITLE_H(22);

    private final int value;

    SizeToken(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}