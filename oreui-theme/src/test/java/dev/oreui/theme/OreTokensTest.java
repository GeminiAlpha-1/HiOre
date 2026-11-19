package dev.oreui.theme;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OreTokensTest {
    @Test
    void darkCoversEveryColorRole() {
        for (ColorRole role : ColorRole.values()) {
            assertDoesNotThrow(() -> OreTokens.DARK.color(role), role.name());
        }
    }

    @Test
    void darkCoversEveryNumericToken() {
        for (Space token : Space.values()) {
            assertDoesNotThrow(() -> OreTokens.DARK.space(token), token.name());
        }
        for (SizeToken token : SizeToken.values()) {
            assertDoesNotThrow(() -> OreTokens.DARK.size(token), token.name());
        }
        for (Radius token : Radius.values()) {
            assertDoesNotThrow(() -> OreTokens.DARK.radius(token), token.name());
        }
        for (Border token : Border.values()) {
            assertDoesNotThrow(() -> OreTokens.DARK.border(token), token.name());
        }
        for (Motion token : Motion.values()) {
            assertDoesNotThrow(() -> OreTokens.DARK.motion(token), token.name());
        }
        for (TypeRole role : TypeRole.values()) {
            assertDoesNotThrow(() -> OreTokens.DARK.type(role), role.name());
        }
    }

    @Test
    void darkCoversAssetsSoundsAndStates() {
        for (AssetKey key : AssetKey.values()) {
            assertDoesNotThrow(() -> OreTokens.DARK.asset(key), key.name());
        }
        for (SoundRole role : SoundRole.values()) {
            assertDoesNotThrow(() -> OreTokens.DARK.sound(role), role.name());
        }
        for (ButtonVariant variant : ButtonVariant.values()) {
            for (ControlState state : ControlState.values()) {
                assertNotNull(OreTokens.DARK.state(variant, state), variant + "/" + state);
            }
        }
    }

    @Test
    void darkMatchesDesignSamples() {
        assertEquals(0x80000000, OreTokens.DARK.color(ColorRole.BACKDROP));
        assertEquals(0xFF313233, OreTokens.DARK.color(ColorRole.SURFACE_BASE));
        assertEquals(0xFF3C8527, OreTokens.DARK.color(ColorRole.ACCENT_DEFAULT));
        assertEquals(0x663C8527, OreTokens.DARK.color(ColorRole.SELECTION));
        assertEquals(0xF2141416, OreTokens.DARK.color(ColorRole.TOOLTIP_BG));
        assertEquals(8, OreTokens.DARK.space(Space.SM));
        assertEquals(20, OreTokens.DARK.size(SizeToken.CONTROL_HEIGHT_MD));
        assertEquals(2, OreTokens.DARK.radius(Radius.SM));
        assertEquals(1, OreTokens.DARK.border(Border.HAIRLINE));
        assertEquals(new TypeStyle(16, 20, ColorRole.TEXT_PRIMARY), OreTokens.DARK.type(TypeRole.TITLE));
        assertEquals(140, OreTokens.DARK.motion(Motion.BASE));
        assertEquals("oreui:sdf_ui", OreTokens.DARK.asset(AssetKey.SHADER_SDF_UI));
        assertEquals("click", OreTokens.DARK.sound(SoundRole.CLICK));
    }
}