package dev.oreui.layout;

import dev.oreui.core.geom.Rect2f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpacerTest {
    @Test
    void growStoresFlexGrow() {
        Spacer spacer = Spacer.grow(2f);
        assertEquals(2f, spacer.style().flexGrow(), 0.0001f);
    }

    @Test
    void spacerConsumesFreeSpace() {
        Spacer spacer = Spacer.grow(1f);
        LayoutNode fixed = new LayoutNode(new LayoutStyle().width(50f).height(20f));
        LayoutNode root = new LayoutNode(new LayoutStyle().alignItems(LayoutStyle.Align.START));
        root.addChild(spacer).addChild(fixed);

        LayoutEngine engine = new LayoutEngine();
        engine.setRoot(root);
        engine.compute(new Rect2f(0f, 0f, 200f, 100f));

        assertEquals(150f, spacer.frame().width(), 0.5f);
        assertEquals(150f, fixed.frame().x(), 0.5f);
    }
}