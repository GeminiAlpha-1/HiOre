package dev.oreui.core.geom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SizeTest {
    @Test
    void reportsAspectAndEmptiness() {
        Size size = new Size(16f, 9f);
        assertFalse(size.isEmpty());
        assertEquals(16f / 9f, size.aspect(), 0.0001f);

        Size empty = new Size(16f, 0f);
        assertTrue(empty.isEmpty());
        assertEquals(0f, empty.aspect(), 0.0001f);
    }

    @Test
    void setAndCopy() {
        Size size = new Size();
        size.set(4f, 2f);
        assertEquals(4f, size.width(), 0.0001f);
        assertEquals(2f, size.height(), 0.0001f);

        Size copy = new Size();
        copy.set(size);
        assertEquals(4f, copy.width(), 0.0001f);
        assertEquals(2f, copy.height(), 0.0001f);
    }
}