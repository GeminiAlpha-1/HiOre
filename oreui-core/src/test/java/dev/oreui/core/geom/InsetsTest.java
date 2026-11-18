package dev.oreui.core.geom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InsetsTest {
    @Test
    void uniformBuildsAllSides() {
        Insets insets = Insets.uniform(2f);
        assertEquals(2f, insets.top(), 0.0001f);
        assertEquals(2f, insets.right(), 0.0001f);
        assertEquals(2f, insets.bottom(), 0.0001f);
        assertEquals(2f, insets.left(), 0.0001f);
        assertEquals(4f, insets.horizontal(), 0.0001f);
        assertEquals(4f, insets.vertical(), 0.0001f);
    }

    @Test
    void setUpdatesAndCopies() {
        Insets insets = new Insets();
        insets.set(1f, 2f, 3f, 4f);
        assertEquals(1f, insets.top(), 0.0001f);
        assertEquals(2f, insets.right(), 0.0001f);
        assertEquals(3f, insets.bottom(), 0.0001f);
        assertEquals(4f, insets.left(), 0.0001f);

        Insets copy = new Insets();
        copy.set(insets);
        assertEquals(6f, copy.horizontal(), 0.0001f);
        assertEquals(4f, copy.vertical(), 0.0001f);
    }
}