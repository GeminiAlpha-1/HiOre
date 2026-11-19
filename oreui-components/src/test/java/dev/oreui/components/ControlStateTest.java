package dev.oreui.components;

import dev.oreui.theme.ButtonVariant;
import dev.oreui.theme.ControlState;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ControlStateTest {
    @Test
    void buttonCoversHoverPressedDisabledFocus() {
        ComponentContext context = TestFixtures.context();
        AtomicInteger clicks = new AtomicInteger();
        OreButton button = new OreButton(context, ButtonVariant.PRIMARY, ControlSize.MD,
                new LiteralText("Run"), clicks::incrementAndGet);
        frame(button, 100f, 20f);

        button.setHovered(true);
        assertEquals(ControlState.HOVER, button.state());
        assertTrue(button.pointerDown(5f, 5f));
        assertEquals(ControlState.PRESSED, button.state());
        assertTrue(button.pointerUp(5f, 5f));
        assertEquals(1, clicks.get());
        button.setHovered(false);
        button.setFocused(true);
        assertEquals(ControlState.FOCUSED, button.state());

        button.setEnabled(false);
        assertEquals(ControlState.DISABLED, button.state());
        assertFalse(button.pointerDown(5f, 5f));
        assertFalse(button.keyPressed(KeyCodes.ENTER, 0));
    }

    @Test
    void disabledControlsIgnorePointerAndKeyboard() {
        ComponentContext context = TestFixtures.context();
        OreToggle toggle = new OreToggle(context);
        frame(toggle, 48f, 16f);
        toggle.setEnabled(false);
        assertFalse(toggle.pointerDown(1f, 1f));
        assertFalse(toggle.keyPressed(KeyCodes.ENTER, 0));

        OreSlider slider = new OreSlider(context, SliderMode.CONTINUOUS);
        frame(slider, 100f, 20f);
        slider.setEnabled(false);
        slider.setValue(0.2f);
        assertFalse(slider.pointerDown(75f, 10f));
        assertEquals(0.2f, slider.value(), 0.01f);

        OreTextField field = new OreTextField(context, InputValidation.TEXT, 8);
        frame(field, 100f, 20f);
        field.setEnabled(false);
        assertFalse(field.charTyped('a'));
        assertEquals("", field.text());
    }

    @Test
    void toggleAndChoiceCoverOnOffStates() {
        ComponentContext context = TestFixtures.context();
        OreToggle toggle = new OreToggle(context);
        frame(toggle, 48f, 16f);
        assertFalse(toggle.isOn());
        assertTrue(toggle.pointerDown(1f, 1f));
        assertTrue(toggle.pointerUp(1f, 1f));
        assertTrue(toggle.isOn());

        OreChoiceBox choice = new OreChoiceBox(context, ChoiceMode.CHECKBOX_MULTI);
        frame(choice, 16f, 16f);
        assertFalse(choice.isSelected());
        assertTrue(choice.pointerDown(1f, 1f));
        assertTrue(choice.pointerUp(1f, 1f));
        assertTrue(choice.isSelected());
    }

    private static void frame(OreWidget widget, float width, float height) {
        widget.style().width(width).height(height);
        widget.layoutNode().frame().set(0f, 0f, width, height);
        widget.syncFrames();
    }
}