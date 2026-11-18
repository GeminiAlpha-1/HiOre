package dev.oreui.theme;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StateStyleTest {
    @Test
    void everyVariantAndStateIsDefined() {
        for (ButtonVariant variant : ButtonVariant.values()) {
            for (ControlState state : ControlState.values()) {
                assertNotNull(StateStyle.ForVariant.of(variant, state), variant + "/" + state);
            }
        }
    }

    @Test
    void primaryStatesFollowDesign() {
        StateStyle idle = StateStyle.ForVariant.of(ButtonVariant.PRIMARY, ControlState.DEFAULT);
        assertEquals(ColorRole.ACCENT_DEFAULT, idle.background());
        assertEquals(ColorRole.TEXT_ON_ACCENT, idle.text());
        assertEquals(0, idle.offsetY());
        assertEquals(Border.LIP_BUTTON.value(), idle.lipHeight());

        StateStyle hover = StateStyle.ForVariant.of(ButtonVariant.PRIMARY, ControlState.HOVER);
        assertEquals(ColorRole.ACCENT_HOVER, hover.background());

        StateStyle pressed = StateStyle.ForVariant.of(ButtonVariant.PRIMARY, ControlState.PRESSED);
        assertEquals(2, pressed.offsetY());
        assertEquals(0, pressed.lipHeight());

        StateStyle disabled = StateStyle.ForVariant.of(ButtonVariant.PRIMARY, ControlState.DISABLED);
        assertEquals(ColorRole.ACCENT_DISABLED, disabled.background());
        assertEquals(ColorRole.TEXT_DISABLED, disabled.text());
    }

    @Test
    void disabledStatesAreFlatAndHideDecorations() {
        for (ButtonVariant variant : ButtonVariant.values()) {
            StateStyle style = StateStyle.ForVariant.of(variant, ControlState.DISABLED);
            assertEquals(2, style.offsetY(), variant.name());
            assertEquals(0, style.lipHeight(), variant.name());
            assertEquals(style.background(), style.bevelLight(), variant.name());
            assertEquals(style.background(), style.bevelDark(), variant.name());
            assertNull(style.focusRing(), variant.name());
        }
    }

    @Test
    void variantPalettesAreDistinct() {
        assertEquals(ColorRole.SECONDARY_DEFAULT,
                StateStyle.ForVariant.of(ButtonVariant.SECONDARY, ControlState.DEFAULT).background());
        assertEquals(ColorRole.TEXT_ON_LIGHT,
                StateStyle.ForVariant.of(ButtonVariant.SECONDARY, ControlState.DEFAULT).text());
        assertEquals(ColorRole.DANGER_DEFAULT,
                StateStyle.ForVariant.of(ButtonVariant.DANGER, ControlState.DEFAULT).background());
        assertEquals(0,
                StateStyle.ForVariant.of(ButtonVariant.GHOST, ControlState.DEFAULT).lipHeight());
    }

    @Test
    void focusedStateKeepsFocusRing() {
        StateStyle focused = StateStyle.ForVariant.of(ButtonVariant.PRIMARY, ControlState.FOCUSED);
        assertEquals(ColorRole.FOCUS_RING, focused.focusRing());
        assertEquals(ColorRole.ACCENT_DEFAULT, focused.background());
    }

    @Test
    void invalidInputsAreRejected() {
        assertThrows(NullPointerException.class, () -> StateStyle.ForVariant.of(null, ControlState.DEFAULT));
        assertThrows(IllegalArgumentException.class,
                () -> new StateStyle(ColorRole.SURFACE_BASE, ColorRole.BEVEL_LIGHT, ColorRole.BEVEL_DARK,
                        ColorRole.SURFACE_BASE, ColorRole.TEXT_PRIMARY, ColorRole.FOCUS_RING, -1, 0));
    }
}