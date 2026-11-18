package dev.oreui.core.pool;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IntIdAllocatorTest {
    @Test
    void allocatesSequentialIds() {
        IntIdAllocator allocator = new IntIdAllocator();
        assertEquals(0, allocator.allocate());
        assertEquals(1, allocator.allocate());
        assertEquals(2, allocator.highWaterMark());
        assertTrue(allocator.isAllocated(0));
        assertTrue(allocator.isAllocated(1));
    }

    @Test
    void releaseRecyclesIds() {
        IntIdAllocator allocator = new IntIdAllocator();
        allocator.allocate();
        allocator.allocate();
        allocator.release(0);
        assertFalse(allocator.isAllocated(0));
        assertEquals(0, allocator.allocate());
        assertEquals(2, allocator.highWaterMark());
    }

    @Test
    void invalidReleaseIsRejected() {
        IntIdAllocator allocator = new IntIdAllocator();
        allocator.allocate();
        allocator.release(0);
        assertThrows(IllegalArgumentException.class, () -> allocator.release(0));
        assertThrows(IllegalArgumentException.class, () -> allocator.release(-1));
        assertThrows(IllegalArgumentException.class, () -> allocator.release(5));
    }

    @Test
    void growsBeyondInitialCapacity() {
        IntIdAllocator allocator = new IntIdAllocator();
        for (int i = 0; i < 100; i++) {
            assertEquals(i, allocator.allocate());
        }
        assertEquals(100, allocator.highWaterMark());
        allocator.release(50);
        assertEquals(50, allocator.allocate());
        assertEquals(100, allocator.highWaterMark());
    }
}