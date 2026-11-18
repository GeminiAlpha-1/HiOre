package dev.oreui.core.geom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Rect2fTest {
    @Test
    void containsUsesHalfOpenBounds() {
        Rect2f rect = new Rect2f(10f, 20f, 30f, 40f);
        assertTrue(rect.contains(10f, 20f));
        assertTrue(rect.contains(39.9f, 59.9f));
        assertFalse(rect.contains(40f, 60f));
        assertFalse(rect.contains(9.9f, 20f));
        assertFalse(rect.contains(10f, 60f));
        assertEquals(40f, rect.right(), 0.0001f);
        assertEquals(60f, rect.bottom(), 0.0001f);
        assertEquals(1200f, rect.area(), 0.0001f);
    }

    @Test
    void offsetMovesBothAxes() {
        Rect2f rect = new Rect2f(10f, 20f, 30f, 40f);
        rect.offset(5f, -3f);
        assertEquals(15f, rect.x(), 0.0001f);
        assertEquals(17f, rect.y(), 0.0001f);
        assertEquals(30f, rect.width(), 0.0001f);
        assertEquals(40f, rect.height(), 0.0001f);
    }

    @Test
    void deflateShrinksAndClamps() {
        Rect2f rect = new Rect2f(10f, 20f, 30f, 40f);
        rect.deflate(new Insets(2f, 3f, 4f, 5f));
        assertEquals(15f, rect.x(), 0.0001f);
        assertEquals(22f, rect.y(), 0.0001f);
        assertEquals(22f, rect.width(), 0.0001f);
        assertEquals(34f, rect.height(), 0.0001f);

        Rect2f clamped = new Rect2f(10f, 10f, 4f, 4f);
        clamped.deflate(Insets.uniform(3f));
        assertEquals(0f, clamped.width(), 0.0001f);
        assertEquals(0f, clamped.height(), 0.0001f);
        assertTrue(clamped.isEmpty());
    }

    @Test
    void intersectProducesOverlap() {
        Rect2f rect = new Rect2f(0f, 0f, 10f, 10f);
        assertTrue(rect.intersect(new Rect2f(5f, 5f, 10f, 10f)));
        assertEquals(5f, rect.x(), 0.0001f);
        assertEquals(5f, rect.y(), 0.0001f);
        assertEquals(5f, rect.width(), 0.0001f);
        assertEquals(5f, rect.height(), 0.0001f);
    }

    @Test
    void intersectEmptiesWhenDisjoint() {
        Rect2f rect = new Rect2f(0f, 0f, 5f, 5f);
        assertFalse(rect.intersect(new Rect2f(10f, 10f, 5f, 5f)));
        assertTrue(rect.isEmpty());
        assertEquals(0f, rect.width(), 0.0001f);
        assertEquals(0f, rect.height(), 0.0001f);
    }

    @Test
    void intersectsDoesNotMutate() {
        Rect2f rect = new Rect2f(0f, 0f, 10f, 10f);
        assertTrue(rect.intersects(new Rect2f(9f, 9f, 5f, 5f)));
        assertEquals(0f, rect.x(), 0.0001f);
        assertEquals(0f, rect.y(), 0.0001f);
        assertEquals(10f, rect.width(), 0.0001f);
        assertEquals(10f, rect.height(), 0.0001f);
    }

    @Test
    void emptyRectHasZeroArea() {
        Rect2f rect = new Rect2f(4f, 4f, 0f, 10f);
        assertTrue(rect.isEmpty());
        assertEquals(0f, rect.area(), 0.0001f);
        assertFalse(rect.contains(4f, 4f));
    }

    @Test
    void setCopiesAnotherRect() {
        Rect2f rect = new Rect2f();
        rect.set(new Rect2f(1f, 2f, 3f, 4f));
        assertEquals(1f, rect.x(), 0.0001f);
        assertEquals(2f, rect.y(), 0.0001f);
        assertEquals(3f, rect.width(), 0.0001f);
        assertEquals(4f, rect.height(), 0.0001f);
    }
}