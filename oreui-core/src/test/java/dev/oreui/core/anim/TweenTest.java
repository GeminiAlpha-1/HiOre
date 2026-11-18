package dev.oreui.core.anim;

import com.sun.management.ThreadMXBean;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.lang.management.ManagementFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TweenTest {
    @Test
    void tweenStartsAtInitialValue() {
        Tween tween = new Tween(3f, 1f, Easings.ORE_STANDARD);
        assertEquals(3f, tween.value(), 0.0001f);
        assertTrue(tween.isFinished());
    }

    @Test
    void tweenReachesTargetAndFinishes() {
        Tween tween = new Tween(0f, 1f, Easings.ORE_STANDARD);
        tween.setTarget(10f);
        assertEquals(10f, tween.target(), 0.0001f);
        assertFalse(tween.isFinished());
        tween.advance(0.5f);
        float halfway = tween.value();
        assertTrue(halfway > 0f && halfway < 10f);
        tween.advance(1f);
        assertEquals(10f, tween.value(), 0.0001f);
        assertTrue(tween.isFinished());
    }

    @Test
    void zeroDeltaDoesNotAdvance() {
        Tween tween = new Tween(0f, 1f, Easings.ORE_STANDARD);
        tween.setTarget(10f);
        tween.advance(0f);
        assertEquals(0f, tween.value(), 0.0001f);
        assertFalse(tween.isFinished());
    }

    @Test
    void largeDeltaSnapsToTarget() {
        Tween tween = new Tween(0f, 1f, Easings.ORE_STANDARD);
        tween.setTarget(10f);
        tween.advance(100f);
        assertEquals(10f, tween.value(), 0.0001f);
        assertTrue(tween.isFinished());
    }

    @Test
    void setTargetRestartsFromCurrentValue() {
        Tween tween = new Tween(0f, 1f, Easings.ORE_STANDARD);
        tween.setTarget(10f);
        tween.advance(0.5f);
        float held = tween.value();
        tween.setTarget(20f);
        assertEquals(held, tween.value(), 0.0001f);
        tween.advance(1f);
        assertEquals(20f, tween.value(), 0.0001f);
    }

    @Test
    void zeroDurationIsImmediate() {
        Tween tween = new Tween(0f, 0f, Easings.ORE_STANDARD);
        tween.setTarget(5f);
        assertEquals(5f, tween.value(), 0.0001f);
        tween.advance(1f);
        assertEquals(5f, tween.value(), 0.0001f);
    }

    @Test
    void snapToTargetIsImmediate() {
        Tween tween = new Tween(0f, 1f, Easings.ORE_STANDARD);
        tween.setTarget(7f);
        tween.snapToTarget();
        assertEquals(7f, tween.value(), 0.0001f);
        assertTrue(tween.isFinished());
    }

    @Test
    void invalidArgumentsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Tween(0f, -1f, Easings.ORE_STANDARD));
        assertThrows(NullPointerException.class, () -> new Tween(0f, 1f, null));
    }

    @Test
    void advanceDoesNotAllocate() {
        ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        Assumptions.assumeTrue(bean.isThreadAllocatedMemorySupported());
        if (!bean.isThreadAllocatedMemoryEnabled()) {
            bean.setThreadAllocatedMemoryEnabled(true);
        }
        Tween tween = new Tween(0f, 1f, Easings.ORE_STANDARD);
        for (int i = 0; i < 200_000; i++) {
            tween.setTarget((i & 1) == 0 ? 1f : 0f);
            tween.advance(0.01f);
        }
        long threadId = Thread.currentThread().threadId();
        long before = bean.getThreadAllocatedBytes(threadId);
        for (int i = 0; i < 200_000; i++) {
            tween.setTarget((i & 1) == 0 ? 1f : 0f);
            tween.advance(0.01f);
        }
        long after = bean.getThreadAllocatedBytes(threadId);
        assertEquals(0L, after - before, "advance allocated " + (after - before) + " bytes");
    }
}