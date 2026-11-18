package dev.oreui.layout;

import dev.oreui.core.geom.Insets;
import dev.oreui.core.geom.Rect2f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LayoutEngineTest {
    private static LayoutNode box(float width, float height) {
        return new LayoutNode(new LayoutStyle().width(width).height(height));
    }

    private static LayoutNode root(LayoutStyle style, LayoutNode... children) {
        LayoutNode root = new LayoutNode(style);
        for (LayoutNode child : children) {
            root.addChild(child);
        }
        return root;
    }

    private static void compute(LayoutNode root, float width, float height) {
        LayoutEngine engine = new LayoutEngine();
        engine.setRoot(root);
        engine.compute(new Rect2f(0f, 0f, width, height));
    }

    @Test
    void rowPlacesChildrenLeftToRight() {
        LayoutNode a = box(50f, 20f);
        LayoutNode b = box(60f, 20f);
        compute(root(new LayoutStyle().alignItems(LayoutStyle.Align.START), a, b), 200f, 100f);
        assertEquals(0f, a.frame().x(), 0.5f);
        assertEquals(50f, b.frame().x(), 0.5f);
        assertEquals(0f, a.frame().y(), 0.5f);
        assertEquals(20f, a.frame().height(), 0.5f);
    }

    @Test
    void gapSeparatesChildren() {
        LayoutNode a = box(50f, 20f);
        LayoutNode b = box(60f, 20f);
        compute(root(new LayoutStyle().alignItems(LayoutStyle.Align.START).gap(10f), a, b), 200f, 100f);
        assertEquals(60f, b.frame().x(), 0.5f);
    }

    @Test
    void columnPlacesChildrenTopToBottom() {
        LayoutNode a = box(20f, 20f);
        LayoutNode b = box(20f, 30f);
        compute(root(new LayoutStyle()
                .direction(LayoutStyle.Direction.COLUMN)
                .alignItems(LayoutStyle.Align.START)
                .gap(5f), a, b), 200f, 200f);
        assertEquals(0f, a.frame().y(), 0.5f);
        assertEquals(25f, b.frame().y(), 0.5f);
    }

    @Test
    void paddingOffsetsContent() {
        LayoutNode child = box(50f, 20f);
        compute(root(new LayoutStyle()
                .alignItems(LayoutStyle.Align.START)
                .padding(new Insets(10f, 10f, 10f, 10f)), child), 200f, 100f);
        assertEquals(10f, child.frame().x(), 0.5f);
        assertEquals(10f, child.frame().y(), 0.5f);
    }

    @Test
    void flexGrowFillsRemainingSpace() {
        LayoutNode fixed = box(50f, 20f);
        LayoutNode grow = new LayoutNode(new LayoutStyle().width(0f).height(20f).flexGrow(1f));
        compute(root(new LayoutStyle().alignItems(LayoutStyle.Align.START), fixed, grow), 200f, 100f);
        assertEquals(150f, grow.frame().width(), 0.5f);
        assertEquals(50f, grow.frame().x(), 0.5f);
    }

    @Test
    void alignCenterCentersCrossAxis() {
        LayoutNode child = box(30f, 20f);
        compute(root(new LayoutStyle().alignItems(LayoutStyle.Align.CENTER), child), 200f, 100f);
        assertEquals(40f, child.frame().y(), 0.5f);
    }

    @Test
    void justifyCenterCentersMainAxis() {
        LayoutNode child = box(50f, 20f);
        compute(root(new LayoutStyle()
                .alignItems(LayoutStyle.Align.START)
                .justifyContent(LayoutStyle.Justify.CENTER), child), 200f, 100f);
        assertEquals(75f, child.frame().x(), 0.5f);
    }

    @Test
    void justifyEndAlignsToTrailingEdge() {
        LayoutNode child = box(50f, 20f);
        compute(root(new LayoutStyle()
                .alignItems(LayoutStyle.Align.START)
                .justifyContent(LayoutStyle.Justify.END), child), 200f, 100f);
        assertEquals(150f, child.frame().x(), 0.5f);
    }

    @Test
    void cleanTreeDoesNotRelayout() {
        LayoutNode child = box(50f, 20f);
        LayoutNode root = root(new LayoutStyle().alignItems(LayoutStyle.Align.START), child);
        LayoutEngine engine = new LayoutEngine();
        engine.setRoot(root);
        Rect2f viewport = new Rect2f(0f, 0f, 200f, 100f);
        engine.compute(viewport);
        int count = engine.layoutCount();
        engine.compute(viewport);
        assertEquals(count, engine.layoutCount());

        child.style().width(80f);
        child.markDirty();
        engine.compute(viewport);
        assertEquals(count + 1, engine.layoutCount());
        assertEquals(80f, child.frame().width(), 0.5f);
    }

    @Test
    void viewportChangeRelayouts() {
        LayoutNode root = root(new LayoutStyle(), box(20f, 20f));
        LayoutEngine engine = new LayoutEngine();
        engine.setRoot(root);
        engine.compute(new Rect2f(0f, 0f, 200f, 100f));
        assertEquals(200f, root.frame().width(), 0.5f);
        engine.compute(new Rect2f(0f, 0f, 300f, 100f));
        assertEquals(300f, root.frame().width(), 0.5f);
        assertEquals(2, engine.layoutCount());
    }

    @Test
    void viewportOriginChangeRelayouts() {
        LayoutNode root = root(new LayoutStyle(), box(20f, 20f));
        LayoutEngine engine = new LayoutEngine();
        engine.setRoot(root);
        engine.compute(new Rect2f(0f, 0f, 200f, 100f));
        engine.compute(new Rect2f(7f, 9f, 200f, 100f));
        assertEquals(7f, root.frame().x(), 0.5f);
        assertEquals(9f, root.frame().y(), 0.5f);
        assertEquals(2, engine.layoutCount());
    }

    @Test
    void viewportOriginOffsetsAbsoluteFrames() {
        LayoutNode child = box(50f, 20f);
        LayoutNode root = root(new LayoutStyle().alignItems(LayoutStyle.Align.START), child);
        LayoutEngine engine = new LayoutEngine();
        engine.setRoot(root);
        engine.compute(new Rect2f(10f, 20f, 200f, 100f));
        assertEquals(10f, root.frame().x(), 0.5f);
        assertEquals(20f, root.frame().y(), 0.5f);
        assertEquals(10f, child.frame().x(), 0.5f);
        assertEquals(20f, child.frame().y(), 0.5f);
    }
}