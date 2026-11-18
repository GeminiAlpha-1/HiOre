package dev.oreui.core.geom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SnapTest {
    @Test
    void edgeSnapsAtIntegerScales() {
        assertEquals(10f, Snap.edge(10.3f, 1f), 0.0001f);
        assertEquals(10f, Snap.edge(10.3f, 2f), 0.0001f);
        assertEquals(10f, Snap.edge(10.3f, 3f), 0.0001f);
        assertEquals(11f, Snap.edge(11f, 2f), 0.0001f);
        assertEquals(-0.5f, Snap.edge(-0.2f, 2f), 0.0001f);
    }

    @Test
    void edgeSupportsAutoFractionalScale() {
        assertEquals(10f, Snap.edge(10.3f, 1.5f), 0.0001f);
        assertEquals(10f, Snap.edge(10f, 1.5f), 0.0001f);
        assertEquals(10f, Snap.edge(10.4f, 1.5f), 0.0001f);
        assertEquals(10.666666f, Snap.edge(10.7f, 1.5f), 0.001f);
    }

    @Test
    void nearDetectsAlignedValues() {
        assertTrue(Snap.near(10f, 2f));
        assertTrue(Snap.near(10.00005f, 2f));
        assertTrue(Snap.near(10.0005f, 2f));
        assertTrue(Snap.near(-0.5f, 2f));
        assertFalse(Snap.near(10.01f, 2f));
        assertFalse(Snap.near(10.2f, 2f));
        assertFalse(Snap.near(10f, 0f));
    }

    @Test
    void invalidScaleIsPassThrough() {
        assertEquals(10.3f, Snap.edge(10.3f, 0f), 0.0001f);
        assertEquals(10.3f, Snap.edge(10.3f, -2f), 0.0001f);
    }
}