package dev.oreui.adapter262;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RenderStackTest {
    @Test
    void beginEndResetsAllDepths() {
        RenderStack stack = new RenderStack();
        stack.begin();
        assertEquals(1, stack.transformDepth());
        assertEquals(0, stack.clipDepth());
        assertEquals(1f, stack.opacity());
        stack.pushTransform();
        stack.pushClip();
        stack.pushOpacity(0.5f);
        stack.end();
        assertEquals(0, stack.transformDepth());
        assertEquals(0, stack.clipDepth());
        assertEquals(1f, stack.opacity());
        stack.end();
        assertEquals(0, stack.transformDepth());
    }

    @Test
    void transformUnderflowIsRejected() {
        RenderStack stack = new RenderStack();
        stack.begin();
        assertThrows(IllegalStateException.class, stack::popTransform);
        stack.pushTransform();
        stack.popTransform();
        assertEquals(1, stack.transformDepth());
    }

    @Test
    void clipOverflowAndUnderflowAreRejected() {
        RenderStack stack = new RenderStack();
        stack.begin();
        assertThrows(IllegalStateException.class, stack::popClip);
        for (int i = 0; i < RenderStack.MAX_DEPTH; i++) {
            stack.pushClip();
        }
        assertThrows(IllegalStateException.class, stack::pushClip);
        for (int i = 0; i < RenderStack.MAX_DEPTH; i++) {
            stack.popClip();
        }
        assertEquals(0, stack.clipDepth());
    }

    @Test
    void opacityMultipliesClampsAndRestores() {
        RenderStack stack = new RenderStack();
        stack.begin();
        stack.pushOpacity(0.5f);
        assertEquals(0.5f, stack.opacity());
        stack.pushOpacity(0f);
        assertEquals(0f, stack.opacity());
        stack.popOpacity();
        assertEquals(0.5f, stack.opacity());
        stack.popOpacity();
        assertEquals(1f, stack.opacity());
    }

    @Test
    void opacityStackBoundariesAreRejected() {
        RenderStack stack = new RenderStack();
        stack.begin();
        assertThrows(IllegalStateException.class, stack::popOpacity);
        for (int i = 0; i < RenderStack.MAX_DEPTH; i++) {
            stack.pushOpacity(0.5f);
        }
        assertThrows(IllegalStateException.class, () -> stack.pushOpacity(0.5f));
    }
}