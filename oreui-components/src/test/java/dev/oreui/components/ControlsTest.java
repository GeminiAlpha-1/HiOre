package dev.oreui.components;

import dev.oreui.components.platform.IInputEvent;
import dev.oreui.components.platform.InputEvent;
import dev.oreui.core.geom.Rect2f;
import dev.oreui.theme.TypeRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ControlsTest {
    @Test
    void toggleAndChoiceActivate() {
        ComponentContext context = TestFixtures.context();
        OreToggle toggle = new OreToggle(context);
        toggle.style().width(48f);
        toggle.layoutNode().frame().set(0f, 0f, 48f, 16f);
        toggle.syncFrames();
        OreChoiceBox choice = new OreChoiceBox(context, ChoiceMode.CHECKBOX_MULTI);
        choice.layoutNode().frame().set(0f, 0f, 16f, 16f);
        choice.syncFrames();
        assertTrue(toggle.pointerDown(1f, 1f));
        assertTrue(toggle.pointerUp(1f, 1f));
        assertTrue(toggle.isOn());
        assertTrue(choice.pointerDown(1f, 1f));
        assertTrue(choice.pointerUp(1f, 1f));
        assertTrue(choice.isSelected());
        assertTrue(toggle.keyPressed(KeyCodes.ENTER, 0));
        assertEquals(false, toggle.isOn());
    }

    @Test
    void sliderSupportsDragAndKeys() {
        ComponentContext context = TestFixtures.context();
        OreSlider slider = new OreSlider(context, SliderMode.CONTINUOUS);
        slider.layoutNode().frame().set(0f, 0f, 100f, 20f);
        slider.syncFrames();
        slider.setValue(0.2f);
        slider.pointerDown(75f, 10f);
        assertEquals(0.75f, slider.value(), 0.01f);
        slider.pointerMove(25f, 10f);
        assertEquals(0.25f, slider.value(), 0.01f);
        slider.pointerUp(25f, 10f);
        slider.keyPressed(KeyCodes.RIGHT, 0);
        assertEquals(0.30f, slider.value(), 0.01f);
    }

    @Test
    void textFieldEditsClipboardAndUndo() {
        ComponentContext context = TestFixtures.context();
        OreTextField field = new OreTextField(context, InputValidation.TEXT, 8);
        field.setFocused(true);
        assertTrue(field.charTyped('a'));
        assertTrue(field.charTyped('b'));
        assertEquals("ab", field.text());
        assertTrue(field.keyPressed(KeyCodes.BACKSPACE, 0));
        assertEquals("a", field.text());
        assertTrue(field.keyPressed(KeyCodes.C, 2));
        assertEquals("a", context.clipboard().get());
        assertTrue(field.keyPressed(KeyCodes.V, 2));
        assertEquals("aa", field.text());
        assertTrue(field.keyPressed(KeyCodes.Z, 2));
        assertEquals("a", field.text());
    }

    @Test
    void labelEllipsisAndWrap() {
        ComponentContext context = TestFixtures.context();
        OreLabel ellipsis = new OreLabel(context, TestFixtures.text("abcdefghij"), TypeRole.BODY,
                LabelOverflow.ELLIPSIS, 24);
        assertEquals(1, ellipsis.lines().size());
        assertTrue(ellipsis.lines().get(0).endsWith("…"));
        OreLabel wrap = new OreLabel(context, TestFixtures.text("abcdefghij"), TypeRole.BODY,
                LabelOverflow.WRAP, 24);
        assertTrue(wrap.lines().size() > 1);
    }
}