package dev.oreui.components;

import dev.oreui.components.platform.IInputEvent;
import dev.oreui.components.platform.InputEvent;
import dev.oreui.core.geom.Rect2f;
import dev.oreui.theme.ButtonVariant;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComponentTreeTest {
    @Test
    void pointerClickActivatesButtonAndFocusWorks() {
        ComponentContext context = TestFixtures.context();
        OreSurface root = new OreSurface(context, SurfaceVariant.PANEL);
        TestFixtures.fillSurface(context, root, 200, 200);
        int[] clicks = {0};
        OreButton button = new OreButton(context, ButtonVariant.PRIMARY, ControlSize.MD,
                TestFixtures.text("Go"), () -> clicks[0]++);
        button.style().width(100f);
        root.addChild(button);

        ComponentTree tree = new ComponentTree(root);
        tree.layout(new Rect2f(0f, 0f, 200f, 200f));
        assertEquals(button, tree.focused());

        assertTrue(tree.dispatch(new InputEvent(IInputEvent.Kind.POINTER_DOWN, 10f, 10f, 0, 0, '\0')));
        assertTrue(tree.dispatch(new InputEvent(IInputEvent.Kind.POINTER_UP, 10f, 10f, 0, 0, '\0')));
        assertEquals(1, clicks[0]);
        assertTrue(tree.dispatch(new InputEvent(IInputEvent.Kind.KEY_DOWN, 0f, 0f, 258, 0, '\0')));
    }
}