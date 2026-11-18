package dev.oreui.adapter262;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.joml.Matrix3x2f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

class SdfRectStateTest {
    @Test
    void scissorIsForwardedToRenderState() {
        ScreenRectangle scissor = new ScreenRectangle(1, 2, 30, 40);
        SdfRectState state = new SdfRectState(null, new Matrix3x2f(),
                0f, 0f, 10f, 10f, 0, 2, 1, scissor);
        assertSame(scissor, state.scissorArea());
    }
}