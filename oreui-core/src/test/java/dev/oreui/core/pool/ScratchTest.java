package dev.oreui.core.pool;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScratchTest {
    @Test
    void getReusesInstance() {
        Scratch<StringBuilder> scratch = new Scratch<>(StringBuilder::new);
        assertFalse(scratch.isInitialized());
        StringBuilder first = scratch.get();
        assertSame(first, scratch.get());
        assertTrue(scratch.isInitialized());
    }

    @Test
    void resetReplacesInstance() {
        Scratch<StringBuilder> scratch = new Scratch<>(StringBuilder::new);
        StringBuilder first = scratch.get();
        scratch.reset();
        assertNotSame(first, scratch.get());
    }

    @Test
    void nullFactoryIsRejected() {
        assertThrows(NullPointerException.class, () -> new Scratch<>(null));
    }
}