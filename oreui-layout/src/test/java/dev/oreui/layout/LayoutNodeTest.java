package dev.oreui.layout;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LayoutNodeTest {
    @Test
    void addChildLinksParent() {
        LayoutNode root = new LayoutNode();
        LayoutNode child = new LayoutNode();
        root.addChild(child);
        assertEquals(root, child.parent());
        assertEquals(1, root.children().size());
        assertTrue(root.isDirty());
    }

    @Test
    void addChildDetachesFromPreviousParent() {
        LayoutNode first = new LayoutNode();
        LayoutNode second = new LayoutNode();
        LayoutNode child = new LayoutNode();
        first.addChild(child);
        second.addChild(child);
        assertEquals(second, child.parent());
        assertTrue(first.children().isEmpty());
    }

    @Test
    void removeChildUnlinks() {
        LayoutNode root = new LayoutNode();
        LayoutNode child = new LayoutNode();
        root.addChild(child);
        assertTrue(root.removeChild(child));
        assertEquals(null, child.parent());
        assertFalse(root.removeChild(child));
    }

    @Test
    void selfChildIsRejected() {
        LayoutNode node = new LayoutNode();
        assertThrows(IllegalArgumentException.class, () -> node.addChild(node));
    }

    @Test
    void setStyleCopiesAndDirties() {
        LayoutNode node = new LayoutNode();
        LayoutStyle style = new LayoutStyle().width(40f).height(20f);
        node.setStyle(style);
        assertEquals(40f, node.style().width(), 0.0001f);
        style.width(99f);
        assertEquals(40f, node.style().width(), 0.0001f);
        assertTrue(node.isDirty());
    }

    @Test
    void dirtyPropagatesToRoot() {
        LayoutNode root = new LayoutNode();
        LayoutNode child = new LayoutNode();
        root.addChild(child);
        root.clearDirty();
        child.clearDirty();
        child.markDirty();
        assertTrue(child.isDirty());
        assertTrue(root.isDirty());
    }

    @Test
    void frameIsReusable() {
        LayoutNode node = new LayoutNode();
        node.frame().set(1f, 2f, 3f, 4f);
        assertEquals(1f, node.frame().x(), 0.0001f);
        assertEquals(4f, node.frame().height(), 0.0001f);
    }
}