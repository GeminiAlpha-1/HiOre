package dev.oreui.theme;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TokenValueTest {
    @Test
    void spacingMatchesDesign() {
        assertEquals(0, Space.NONE.value());
        assertEquals(2, Space.XXS.value());
        assertEquals(4, Space.XS.value());
        assertEquals(8, Space.SM.value());
        assertEquals(12, Space.MD.value());
        assertEquals(16, Space.LG.value());
        assertEquals(24, Space.XL.value());
        assertEquals(32, Space.XXL.value());
    }

    @Test
    void sizesMatchDesign() {
        assertEquals(16, SizeToken.CONTROL_HEIGHT_SM.value());
        assertEquals(20, SizeToken.CONTROL_HEIGHT_MD.value());
        assertEquals(24, SizeToken.CONTROL_HEIGHT_LG.value());
        assertEquals(18, SizeToken.SLOT_STANDARD.value());
        assertEquals(26, SizeToken.SLOT_LARGE.value());
        assertEquals(200, SizeToken.TOOLTIP_MAX_WIDTH.value());
        assertEquals(320, SizeToken.MODAL_W_MD.value());
    }

    @Test
    void radiusAndBorderMatchDesign() {
        assertEquals(0, Radius.NONE.value());
        assertEquals(2, Radius.SM.value());
        assertEquals(3, Radius.MD.value());
        assertEquals(4, Radius.LG.value());
        assertEquals(999, Radius.PILL.value());
        assertEquals(1, Border.HAIRLINE.value());
        assertEquals(3, Border.LIP_PANEL.value());
    }

    @Test
    void typeRolesMatchDesign() {
        assertEquals(16, TypeRole.TITLE.fontSize());
        assertEquals(20, TypeRole.TITLE.lineHeight());
        assertEquals(ColorRole.TEXT_PRIMARY, TypeRole.TITLE.color());
        assertEquals(9, TypeRole.BODY.fontSize());
        assertEquals(12, TypeRole.BODY.lineHeight());
        assertEquals(8, TypeRole.MONO.fontSize());
        assertEquals(11, TypeRole.MONO.lineHeight());
        assertEquals(new TypeStyle(7, 9, ColorRole.TEXT_MUTED), TypeRole.CAPTION.toStyle());
    }

    @Test
    void motionMatchesDesign() {
        assertEquals(0, Motion.INSTANT.millis());
        assertEquals(80, Motion.FAST.millis());
        assertEquals(140, Motion.BASE.millis());
        assertEquals(3500, Motion.TOAST_DWELL.millis());
        assertEquals(1200, Motion.INDETERMINATE_PERIOD.millis());
    }

    @Test
    void valueObjectsRejectInvalidValues() {
        assertThrows(IllegalArgumentException.class, () -> new TypeStyle(0, 10, ColorRole.TEXT_PRIMARY));
        assertThrows(NullPointerException.class, () -> new TypeStyle(8, 10, null));
        assertThrows(NullPointerException.class,
                () -> new StateStyle(null, ColorRole.BEVEL_LIGHT, ColorRole.BEVEL_DARK,
                        ColorRole.SURFACE_BASE, ColorRole.TEXT_PRIMARY, ColorRole.FOCUS_RING, 0, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new StateStyle(ColorRole.SURFACE_BASE, ColorRole.BEVEL_LIGHT, ColorRole.BEVEL_DARK,
                        ColorRole.SURFACE_BASE, ColorRole.TEXT_PRIMARY, ColorRole.FOCUS_RING, -1, 0));
    }
}