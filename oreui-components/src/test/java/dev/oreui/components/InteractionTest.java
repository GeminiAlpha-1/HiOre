package dev.oreui.components;

import dev.oreui.components.platform.IInputEvent;
import dev.oreui.components.platform.InputEvent;
import dev.oreui.core.geom.Rect2f;
import dev.oreui.theme.ButtonVariant;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InteractionTest {
    @Test
    void buttonRespondsToClickAndKeyboard() {
        ComponentContext context = TestFixtures.context();
        AtomicInteger clicks = new AtomicInteger();
        OreButton button = new OreButton(context, ButtonVariant.PRIMARY, ControlSize.MD,
                TestFixtures.text("Run"), clicks::incrementAndGet);
        button.style().width(100f);
        ComponentTree tree = tree(button);
        Rect2f bounds = button.bounds();

        assertTrue(tree.dispatch(new InputEvent(IInputEvent.Kind.POINTER_DOWN,
                bounds.x() + bounds.width() * 0.5f, bounds.y() + bounds.height() * 0.5f, 0, 0, 0)));
        assertTrue(tree.dispatch(new InputEvent(IInputEvent.Kind.POINTER_UP,
                bounds.x() + bounds.width() * 0.5f, bounds.y() + bounds.height() * 0.5f, 0, 0, 0)));
        assertEquals(1, clicks.get());

        assertTrue(tree.dispatch(new InputEvent(IInputEvent.Kind.KEY_DOWN, 0f, 0f,
                KeyCodes.ENTER, 0, 0)));
        assertEquals(2, clicks.get());
    }

    @Test
    void sliderRespondsToDrag() {
        ComponentContext context = TestFixtures.context();
        OreSlider slider = new OreSlider(context, SliderMode.CONTINUOUS);
        slider.style().width(100f);
        ComponentTree tree = tree(slider);
        Rect2f bounds = slider.bounds();
        float y = bounds.y() + bounds.height() * 0.5f;

        assertTrue(tree.dispatch(new InputEvent(IInputEvent.Kind.POINTER_DOWN,
                bounds.x() + bounds.width() * 0.75f, y, 0, 0, 0)));
        assertTrue(tree.dispatch(new InputEvent(IInputEvent.Kind.POINTER_MOVE,
                bounds.x() + bounds.width() * 0.25f, y, 0, 0, 0)));
        assertTrue(tree.dispatch(new InputEvent(IInputEvent.Kind.POINTER_UP,
                bounds.x() + bounds.width() * 0.25f, y, 0, 0, 0)));
        assertEquals(0.25f, slider.value(), 0.02f);
    }

    @Test
    void textFieldAcceptsKeyboardInput() {
        ComponentContext context = TestFixtures.context();
        OreTextField field = new OreTextField(context, InputValidation.TEXT, 16);
        field.style().width(120f);
        ComponentTree tree = tree(field);
        Rect2f bounds = field.bounds();

        tree.dispatch(new InputEvent(IInputEvent.Kind.POINTER_DOWN,
                bounds.x() + 2f, bounds.y() + bounds.height() * 0.5f, 0, 0, 0));
        tree.dispatch(new InputEvent(IInputEvent.Kind.CHAR_TYPED, 0f, 0f, 0, 0, 'a'));
        tree.dispatch(new InputEvent(IInputEvent.Kind.CHAR_TYPED, 0f, 0f, 0, 0, 0x4E2D));
        assertEquals("a中", field.text());

        tree.dispatch(new InputEvent(IInputEvent.Kind.KEY_DOWN, 0f, 0f,
                KeyCodes.BACKSPACE, 0, 0));
        assertEquals("a", field.text());
    }

    @Test
    void toggleRespondsToClick() {
        ComponentContext context = TestFixtures.context();
        OreToggle toggle = new OreToggle(context);
        toggle.style().width(48f);
        ComponentTree tree = tree(toggle);
        Rect2f bounds = toggle.bounds();

        tree.dispatch(new InputEvent(IInputEvent.Kind.POINTER_DOWN,
                bounds.x() + bounds.width() * 0.5f, bounds.y() + bounds.height() * 0.5f, 0, 0, 0));
        tree.dispatch(new InputEvent(IInputEvent.Kind.POINTER_UP,
                bounds.x() + bounds.width() * 0.5f, bounds.y() + bounds.height() * 0.5f, 0, 0, 0));
        assertTrue(toggle.isOn());
    }

    private static ComponentTree tree(OreWidget widget) {
        OreSurface root = new OreSurface(widget.context, SurfaceVariant.PANEL);
        root.style().width(240f).height(180f);
        root.addChild(widget);
        ComponentTree tree = new ComponentTree(root);
        tree.layout(new Rect2f(0f, 0f, 240f, 180f));
        return tree;
    }
}