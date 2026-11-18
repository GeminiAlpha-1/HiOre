package dev.oreui.core.anim;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EasingsTest {
    @Test
    void standardEasingHitsEndpoints() {
        assertEquals(0f, Easings.ORE_STANDARD.apply(0f), 0.0001f);
        assertEquals(1f, Easings.ORE_STANDARD.apply(1f), 0.0001f);
        assertEquals(0f, Easings.ORE_STANDARD.apply(-1f), 0.0001f);
        assertEquals(1f, Easings.ORE_STANDARD.apply(2f), 0.0001f);
    }

    @Test
    void standardEasingIsMonotonic() {
        float previous = 0f;
        for (int i = 0; i <= 100; i++) {
            float value = Easings.ORE_STANDARD.apply(i / 100f);
            assertTrue(value + 0.0001f >= previous);
            previous = value;
        }
    }

    @Test
    void allPresetsStayInsideUnitRange() {
        for (int i = 0; i <= 100; i++) {
            float t = i / 100f;
            float standard = Easings.ORE_STANDARD.apply(t);
            float enter = Easings.ORE_ENTER.apply(t);
            float exit = Easings.ORE_EXIT.apply(t);
            assertTrue(standard >= 0f && standard <= 1f);
            assertTrue(enter >= 0f && enter <= 1f);
            assertTrue(exit >= 0f && exit <= 1f);
        }
    }

    @Test
    void invalidControlPointsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> Easings.cubicBezier(-0.1f, 0f, 0f, 1f));
        assertThrows(IllegalArgumentException.class, () -> Easings.cubicBezier(0f, 0f, 1.1f, 1f));
    }
}