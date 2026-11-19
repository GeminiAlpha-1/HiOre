package dev.oreui.theme;

public final class OreTokens {
    public static final OreTheme DARK = buildDark();

    private OreTokens() {
    }

    private static OreTheme buildDark() {
        OreTheme.Builder builder = OreTheme.builder();
        builder.override(ColorRole.BACKDROP, 0x80000000);
        builder.override(ColorRole.SURFACE_BASE, 0xFF313233);
        builder.override(ColorRole.SURFACE_CARD, 0xFF3C3D3E);
        builder.override(ColorRole.SURFACE_RAISED_DEFAULT, 0xFF48494A);
        builder.override(ColorRole.SURFACE_RAISED_HOVER, 0xFF535455);
        builder.override(ColorRole.SURFACE_RAISED_PRESSED, 0xFF3E3F40);
        builder.override(ColorRole.SURFACE_RAISED_DISABLED, 0xFF3A3B3C);
        builder.override(ColorRole.SURFACE_SUNKEN, 0xFF1E1E1F);
        builder.override(ColorRole.BORDER_OUTER, 0xFF141415);
        builder.override(ColorRole.BEVEL_LIGHT, 0xFF6E6F70);
        builder.override(ColorRole.BEVEL_DARK, 0xFF202021);
        builder.override(ColorRole.WELL_SHADOW, 0xFF121213);
        builder.override(ColorRole.WELL_RIM, 0xFF5A5B5C);
        builder.override(ColorRole.ACCENT_DEFAULT, 0xFF3C8527);
        builder.override(ColorRole.ACCENT_HOVER, 0xFF4CA632);
        builder.override(ColorRole.ACCENT_PRESSED, 0xFF2A641C);
        builder.override(ColorRole.ACCENT_DISABLED, 0xFF2E4A26);
        builder.override(ColorRole.ACCENT_LIP, 0xFF1D4D13);
        builder.override(ColorRole.ACCENT_HIGHLIGHT, 0xFF639D52);
        builder.override(ColorRole.ACCENT_HIGHLIGHT_PRESSED, 0xFF3F7333);
        builder.override(ColorRole.SECONDARY_DEFAULT, 0xFFD0D1D4);
        builder.override(ColorRole.SECONDARY_HOVER, 0xFFE3E4E6);
        builder.override(ColorRole.SECONDARY_PRESSED, 0xFFB4B5B8);
        builder.override(ColorRole.SECONDARY_DISABLED, 0xFF7C7D80);
        builder.override(ColorRole.SECONDARY_LIP, 0xFF58585A);
        builder.override(ColorRole.SECONDARY_HIGHLIGHT, 0xFFE2E3E5);
        builder.override(ColorRole.SECONDARY_HIGHLIGHT_PRESSED, 0xFFC1C2C4);
        builder.override(ColorRole.DANGER_DEFAULT, 0xFFC33636);
        builder.override(ColorRole.DANGER_HOVER, 0xFFD64A4A);
        builder.override(ColorRole.DANGER_PRESSED, 0xFFA32A2A);
        builder.override(ColorRole.DANGER_DISABLED, 0xFF6E3A3A);
        builder.override(ColorRole.DANGER_LIP, 0xFFAD1D1D);
        builder.override(ColorRole.DANGER_HIGHLIGHT, 0xFFCF4A4A);
        builder.override(ColorRole.DANGER_HIGHLIGHT_PRESSED, 0xFFC64242);
        builder.override(ColorRole.TEXT_PRIMARY, 0xFFFFFFFF);
        builder.override(ColorRole.TEXT_SECONDARY, 0xFFD0D1D4);
        builder.override(ColorRole.TEXT_MUTED, 0xFFA0A2A6);
        builder.override(ColorRole.TEXT_DISABLED, 0xFF6E7073);
        builder.override(ColorRole.TEXT_ON_LIGHT, 0xFF1E1E1F);
        builder.override(ColorRole.TEXT_ON_ACCENT, 0xFFFFFFFF);
        builder.override(ColorRole.TEXT_SHADOW, 0x59000000);
        builder.override(ColorRole.FOCUS_RING, 0xFFFFFFFF);
        builder.override(ColorRole.TOOLTIP_BG, 0xF2141416);
        builder.override(ColorRole.TOOLTIP_BORDER, 0xFF525461);
        builder.override(ColorRole.TOAST_INFO_ACCENT, 0xFF2E89FF);
        builder.override(ColorRole.TOAST_SUCCESS_ACCENT, 0xFF3C8527);
        builder.override(ColorRole.TOAST_WARNING_ACCENT, 0xFFF5A623);
        builder.override(ColorRole.TOAST_ERROR_ACCENT, 0xFFC33636);
        builder.override(ColorRole.SCROLL_TRACK, 0x40000000);
        builder.override(ColorRole.SCROLL_THUMB, 0xFF6E7073);
        builder.override(ColorRole.SCROLL_THUMB_HOVER, 0xFF8A8C8F);
        builder.override(ColorRole.SLIDER_FILL, 0xFF3C8527);
        builder.override(ColorRole.SLIDER_KNOB, 0xFFD0D1D4);
        builder.override(ColorRole.SLIDER_KNOB_HOVER, 0xFFFFFFFF);
        builder.override(ColorRole.SLIDER_TRACK, 0xFF8C8D90);
        builder.override(ColorRole.SLIDER_TRACK_HIGHLIGHT, 0xFFA3A4A6);
        builder.override(ColorRole.SLIDER_THUMB_HIGHLIGHT, 0xFFE2E3E5);
        builder.override(ColorRole.SLIDER_THUMB_SHADOW, 0xFF58585A);
        builder.override(ColorRole.SWITCH_TRACK, 0xFF8C8D90);
        builder.override(ColorRole.SWITCH_TRACK_HIGHLIGHT, 0xFFA3A4A6);
        builder.override(ColorRole.SWITCH_THUMB, 0xFFD0D1D4);
        builder.override(ColorRole.SWITCH_THUMB_HIGHLIGHT, 0xFFE2E3E5);
        builder.override(ColorRole.SWITCH_THUMB_SHADOW, 0xFF58585A);
        builder.override(ColorRole.CHECKBOX_TRACK, 0xFF8C8D90);
        builder.override(ColorRole.CHECKBOX_TRACK_HIGHLIGHT, 0xFFA3A4A6);
        builder.override(ColorRole.PROGRESS_FILL, 0xFF43A02A);
        builder.override(ColorRole.PROGRESS_FILL_LIP, 0xFF2A641C);
        builder.override(ColorRole.CARET, 0xFFFFFFFF);
        builder.override(ColorRole.SELECTION, 0x663C8527);
        builder.override(ColorRole.SLOT_HOVER, 0x66FFFFFF);
        builder.override(ColorRole.GAUGE_TICK, 0x99FFFFFF);
        builder.override(ColorRole.DIVIDER_DARK, 0xFF313233);
        builder.override(ColorRole.DIVIDER_LIGHT, 0xFF58585A);
        builder.override(ColorRole.ICON_PRIMARY, 0xFFFFFFFF);
        builder.override(ColorRole.ICON_MUTED, 0xFFA0A2A6);

        for (Space token : Space.values()) {
            builder.override(token, token.value());
        }
        for (SizeToken token : SizeToken.values()) {
            builder.override(token, token.value());
        }
        for (Radius token : Radius.values()) {
            builder.override(token, token.value());
        }
        for (Border token : Border.values()) {
            builder.override(token, token.value());
        }
        for (TypeRole role : TypeRole.values()) {
            builder.override(role, role.toStyle());
        }
        for (Motion token : Motion.values()) {
            builder.override(token, token.millis());
        }

        builder.override(AssetKey.PANEL_NINE_SLICE, "oreui:panel_nine_slice");
        builder.override(AssetKey.SHADER_SDF_UI, "oreui:sdf_ui");
        builder.override(SoundRole.CLICK, "click");
        builder.override(SoundRole.TOGGLE_ON, "toggle_on");
        builder.override(SoundRole.TOGGLE_OFF, "toggle_off");
        builder.override(SoundRole.TYPING, "typing");
        builder.override(SoundRole.POPUP_OPEN, "popup_open");
        builder.override(SoundRole.POPUP_CLOSE, "popup_close");
        builder.override(SoundRole.MODAL_OPEN, "modal_open");
        builder.override(SoundRole.TOAST, "toast");
        builder.override(SoundRole.ERROR, "error");
        builder.override(SoundRole.SCROLL, "scroll");
        return builder.build();
    }
}