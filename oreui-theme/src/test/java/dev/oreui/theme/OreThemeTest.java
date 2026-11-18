package dev.oreui.theme;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OreThemeTest {
    @Test
    void deriveOverridesWithoutMutatingSource() {
        int originalColor = OreTokens.DARK.color(ColorRole.ACCENT_DEFAULT);
        int originalSpace = OreTokens.DARK.space(Space.SM);
        int originalMotion = OreTokens.DARK.motion(Motion.BASE);
        TypeStyle originalType = OreTokens.DARK.type(TypeRole.BODY);
        StateStyle originalState = OreTokens.DARK.state(ButtonVariant.PRIMARY, ControlState.DEFAULT);
        StateStyle custom = new StateStyle(ColorRole.DANGER_DEFAULT, ColorRole.DANGER_HOVER,
                ColorRole.DANGER_PRESSED, ColorRole.DANGER_LIP, ColorRole.TEXT_ON_ACCENT,
                ColorRole.FOCUS_RING, 1, 1);

        OreTheme derived = OreTokens.DARK.derive()
                .override(ColorRole.ACCENT_DEFAULT, 0xFF112233)
                .override(Space.SM, 99)
                .override(Motion.BASE, 1)
                .override(TypeRole.BODY, new TypeStyle(10, 14, ColorRole.TEXT_SECONDARY))
                .overrideState(ButtonVariant.PRIMARY, ControlState.DEFAULT, custom)
                .build();

        assertEquals(0xFF112233, derived.color(ColorRole.ACCENT_DEFAULT));
        assertEquals(originalColor, OreTokens.DARK.color(ColorRole.ACCENT_DEFAULT));
        assertEquals(99, derived.space(Space.SM));
        assertEquals(originalSpace, OreTokens.DARK.space(Space.SM));
        assertEquals(1, derived.motion(Motion.BASE));
        assertEquals(originalMotion, OreTokens.DARK.motion(Motion.BASE));
        assertNotEquals(originalType, derived.type(TypeRole.BODY));
        assertEquals(originalType, OreTokens.DARK.type(TypeRole.BODY));
        assertEquals(custom, derived.state(ButtonVariant.PRIMARY, ControlState.DEFAULT));
        assertEquals(originalState, OreTokens.DARK.state(ButtonVariant.PRIMARY, ControlState.DEFAULT));
    }

    @Test
    void deriveCanOverrideAssetsAndSounds() {
        OreTheme derived = OreTokens.DARK.derive()
                .override(AssetKey.SHADER_SDF_UI, "test:sdf")
                .override(SoundRole.CLICK, "test:click")
                .build();
        assertEquals("test:sdf", derived.asset(AssetKey.SHADER_SDF_UI));
        assertEquals("test:click", derived.sound(SoundRole.CLICK));
    }

    @Test
    void partialThemeFailsOnMissingToken() {
        OreTheme empty = OreTheme.builder().build();
        assertThrows(IllegalStateException.class, () -> empty.color(ColorRole.BACKDROP));
        assertThrows(IllegalStateException.class, () -> empty.space(Space.SM));
    }
}