package dev.oreui.theme;

public record StateStyle(
        ColorRole background,
        ColorRole bevelLight,
        ColorRole bevelDark,
        ColorRole lip,
        ColorRole text,
        ColorRole focusRing,
        int offsetY,
        int lipHeight) {

    public StateStyle {
        if (background == null || bevelLight == null || bevelDark == null
                || lip == null || text == null) {
            throw new NullPointerException("state colour roles");
        }
        if (offsetY < 0 || lipHeight < 0) {
            throw new IllegalArgumentException("offsetY and lipHeight must be non-negative");
        }
    }

    public static final class ForVariant {
        private ForVariant() {
        }

        public static StateStyle of(ButtonVariant variant, ControlState state) {
            if (variant == null || state == null) {
                throw new NullPointerException("variant/state");
            }
            return switch (variant) {
                case PRIMARY -> primary(state);
                case SECONDARY -> secondary(state);
                case DANGER -> danger(state);
                case HERO -> hero(state);
                case GHOST -> ghost(state);
            };
        }

        private static StateStyle primary(ControlState state) {
            return switch (state) {
                case DEFAULT, FOCUSED -> style(ColorRole.ACCENT_DEFAULT, ColorRole.ACCENT_HOVER,
                        ColorRole.ACCENT_PRESSED, ColorRole.ACCENT_LIP, ColorRole.TEXT_ON_ACCENT,
                        0, Border.LIP_BUTTON.value());
                case HOVER -> style(ColorRole.ACCENT_HOVER, ColorRole.ACCENT_HOVER,
                        ColorRole.ACCENT_PRESSED, ColorRole.ACCENT_LIP, ColorRole.TEXT_ON_ACCENT,
                        0, Border.LIP_BUTTON.value());
                case PRESSED -> style(ColorRole.ACCENT_PRESSED, ColorRole.ACCENT_PRESSED,
                        ColorRole.ACCENT_DEFAULT, ColorRole.ACCENT_PRESSED, ColorRole.TEXT_ON_ACCENT,
                        2, 0);
                case DISABLED -> disabled(ColorRole.ACCENT_DISABLED, ColorRole.TEXT_DISABLED);
            };
        }

        private static StateStyle secondary(ControlState state) {
            return switch (state) {
                case DEFAULT, FOCUSED -> style(ColorRole.SECONDARY_DEFAULT, ColorRole.BEVEL_LIGHT,
                        ColorRole.BEVEL_DARK, ColorRole.SECONDARY_LIP, ColorRole.TEXT_ON_LIGHT,
                        0, Border.LIP_BUTTON.value());
                case HOVER -> style(ColorRole.SECONDARY_HOVER, ColorRole.BEVEL_LIGHT,
                        ColorRole.BEVEL_DARK, ColorRole.SECONDARY_LIP, ColorRole.TEXT_ON_LIGHT,
                        0, Border.LIP_BUTTON.value());
                case PRESSED -> style(ColorRole.SECONDARY_PRESSED, ColorRole.BEVEL_DARK,
                        ColorRole.BEVEL_LIGHT, ColorRole.SECONDARY_PRESSED, ColorRole.TEXT_ON_LIGHT,
                        2, 0);
                case DISABLED -> disabled(ColorRole.SECONDARY_DISABLED, ColorRole.TEXT_DISABLED);
            };
        }

        private static StateStyle danger(ControlState state) {
            return switch (state) {
                case DEFAULT, FOCUSED -> style(ColorRole.DANGER_DEFAULT, ColorRole.DANGER_HOVER,
                        ColorRole.DANGER_PRESSED, ColorRole.DANGER_LIP, ColorRole.TEXT_ON_ACCENT,
                        0, Border.LIP_BUTTON.value());
                case HOVER -> style(ColorRole.DANGER_HOVER, ColorRole.DANGER_HOVER,
                        ColorRole.DANGER_PRESSED, ColorRole.DANGER_LIP, ColorRole.TEXT_ON_ACCENT,
                        0, Border.LIP_BUTTON.value());
                case PRESSED -> style(ColorRole.DANGER_PRESSED, ColorRole.DANGER_PRESSED,
                        ColorRole.DANGER_DEFAULT, ColorRole.DANGER_PRESSED, ColorRole.TEXT_ON_ACCENT,
                        2, 0);
                case DISABLED -> disabled(ColorRole.DANGER_DISABLED, ColorRole.TEXT_DISABLED);
            };
        }

        private static StateStyle hero(ControlState state) {
            return switch (state) {
                case DEFAULT, FOCUSED -> style(ColorRole.ACCENT_DEFAULT, ColorRole.ACCENT_HOVER,
                        ColorRole.ACCENT_PRESSED, ColorRole.ACCENT_LIP, ColorRole.TEXT_ON_ACCENT,
                        0, Border.LIP_BUTTON.value());
                case HOVER -> style(ColorRole.ACCENT_HOVER, ColorRole.ACCENT_HOVER,
                        ColorRole.ACCENT_PRESSED, ColorRole.ACCENT_LIP, ColorRole.TEXT_ON_ACCENT,
                        0, Border.LIP_BUTTON.value());
                case PRESSED -> style(ColorRole.ACCENT_PRESSED, ColorRole.ACCENT_PRESSED,
                        ColorRole.ACCENT_DEFAULT, ColorRole.ACCENT_PRESSED, ColorRole.TEXT_ON_ACCENT,
                        2, 0);
                case DISABLED -> disabled(ColorRole.ACCENT_DISABLED, ColorRole.TEXT_DISABLED);
            };
        }

        private static StateStyle ghost(ControlState state) {
            return switch (state) {
                case DEFAULT, FOCUSED -> style(ColorRole.SURFACE_BASE, ColorRole.SURFACE_BASE,
                        ColorRole.SURFACE_BASE, ColorRole.SURFACE_BASE, ColorRole.TEXT_PRIMARY,
                        0, 0);
                case HOVER -> style(ColorRole.SURFACE_RAISED_HOVER, ColorRole.SURFACE_RAISED_HOVER,
                        ColorRole.SURFACE_RAISED_HOVER, ColorRole.SURFACE_RAISED_HOVER,
                        ColorRole.TEXT_PRIMARY, 0, 0);
                case PRESSED -> style(ColorRole.SURFACE_RAISED_PRESSED, ColorRole.SURFACE_RAISED_PRESSED,
                        ColorRole.SURFACE_RAISED_PRESSED, ColorRole.SURFACE_RAISED_PRESSED,
                        ColorRole.TEXT_PRIMARY, 0, 0);
                case DISABLED -> disabled(ColorRole.SURFACE_RAISED_DISABLED, ColorRole.TEXT_DISABLED);
            };
        }

        private static StateStyle style(ColorRole background, ColorRole bevelLight, ColorRole bevelDark,
                                        ColorRole lip, ColorRole text, int offsetY, int lipHeight) {
            return new StateStyle(background, bevelLight, bevelDark, lip, text,
                    ColorRole.FOCUS_RING, offsetY, lipHeight);
        }

        private static StateStyle disabled(ColorRole background, ColorRole text) {
            return new StateStyle(background, background, background, background, text, null, 2, 0);
        }
    }
}