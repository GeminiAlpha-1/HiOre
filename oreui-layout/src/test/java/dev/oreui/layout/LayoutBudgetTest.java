package dev.oreui.layout;

import dev.oreui.core.geom.Rect2f;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LayoutBudgetTest {
    private static final int WARMUP_RUNS = 400;
    private static final int SAMPLE_RUNS = 400;
    private static final int DIRTY_ROUNDS = 5;
    private static final int COLUMNS = 9;
    private static final int MIXED_FRAMES = 1000;
    private static final int DIRTY_EVERY = 10;
    private static final float BUDGET_300_NODES_MS = 0.2f;
    private static final float BUDGET_1000_NODES_MS = 0.5f;
    private static final boolean CI = System.getenv("CI") != null;
    private static final float DIRTY_CEILING_300_MS = CI ? 2f : BUDGET_300_NODES_MS;
    private static final float DIRTY_CEILING_1000_MS = CI ? 8f : BUDGET_1000_NODES_MS;

    @Test
    void steadyStateThreeHundredNodesWithinBudget() {
        steady(300, BUDGET_300_NODES_MS);
    }

    @Test
    void steadyStateThousandNodesWithinBudget() {
        steady(1000, BUDGET_1000_NODES_MS);
    }

    @Test
    void mixedFrameAverageWithinBudget() {
        mixed(300, BUDGET_300_NODES_MS);
        mixed(1000, BUDGET_1000_NODES_MS);
    }

    @Test
    void dirtyRebuildBaselineStaysWithinCeiling() {
        dirty(300, DIRTY_CEILING_300_MS);
        dirty(1000, DIRTY_CEILING_1000_MS);
    }

    private static void steady(int leaves, float budgetMs) {
        LayoutEngine engine = new LayoutEngine();
        LayoutNode root = grid(leaves);
        engine.setRoot(root);
        Rect2f viewport = new Rect2f(0f, 0f, 320f, 240f);
        engine.compute(viewport);
        int completed = engine.layoutCount();
        float[] samples = new float[SAMPLE_RUNS];
        for (int i = 0; i < SAMPLE_RUNS; i++) {
            long start = System.nanoTime();
            engine.compute(viewport);
            samples[i] = (System.nanoTime() - start) / 1_000_000f;
        }
        Arrays.sort(samples);
        float median = samples[SAMPLE_RUNS / 2];
        assertEquals(completed, engine.layoutCount(), "steady-state compute must reuse the previous layout");
        System.out.printf("layout.%d.leaves.steadyMedianMs=%.5f%n", leaves, median);
        assertTrue(median <= budgetMs,
                "steady-state " + median + "ms exceeds budget " + budgetMs + "ms for " + leaves + " leaves");
    }

    private static void mixed(int leaves, float budgetMs) {
        LayoutEngine engine = new LayoutEngine();
        LayoutNode root = grid(leaves);
        engine.setRoot(root);
        Rect2f viewport = new Rect2f(0f, 0f, 320f, 240f);
        for (int i = 0; i < WARMUP_RUNS; i++) {
            if (i % DIRTY_EVERY == 0) {
                engine.requestLayout();
            }
            engine.compute(viewport);
        }
        long start = System.nanoTime();
        for (int i = 0; i < MIXED_FRAMES; i++) {
            if (i % DIRTY_EVERY == 0) {
                engine.requestLayout();
            }
            engine.compute(viewport);
        }
        float average = (System.nanoTime() - start) / 1_000_000f / MIXED_FRAMES;
        System.out.printf("layout.%d.leaves.mixedAvgMs=%.5f%n", leaves, average);
        assertTrue(average <= budgetMs,
                "mixed-frame average " + average + "ms exceeds budget " + budgetMs
                        + "ms for " + leaves + " leaves");
    }

    private static void dirty(int leaves, float ceilingMs) {
        LayoutEngine engine = new LayoutEngine();
        LayoutNode root = grid(leaves);
        engine.setRoot(root);
        Rect2f viewport = new Rect2f(0f, 0f, 320f, 240f);
        for (int i = 0; i < WARMUP_RUNS; i++) {
            engine.requestLayout();
            engine.compute(viewport);
        }
        float best = Float.MAX_VALUE;
        for (int round = 0; round < DIRTY_ROUNDS; round++) {
            float[] samples = new float[SAMPLE_RUNS];
            for (int i = 0; i < SAMPLE_RUNS; i++) {
                engine.requestLayout();
                long start = System.nanoTime();
                engine.compute(viewport);
                samples[i] = (System.nanoTime() - start) / 1_000_000f;
            }
            Arrays.sort(samples);
            best = Math.min(best, samples[SAMPLE_RUNS / 2]);
        }
        System.out.printf("layout.%d.leaves.dirtyMedianMs=%.4f%n", leaves, best);
        assertTrue(best <= ceilingMs,
                "dirty rebuild " + best + "ms exceeds ceiling " + ceilingMs + "ms for " + leaves + " leaves");
    }

    private static LayoutNode grid(int leaves) {
        LayoutNode root = new LayoutNode();
        root.style().direction(LayoutStyle.Direction.COLUMN);
        LayoutNode row = new LayoutNode();
        row.style().direction(LayoutStyle.Direction.ROW);
        root.addChild(row);
        int placed = 0;
        for (int i = 0; i < leaves; i++) {
            LayoutNode leaf = new LayoutNode();
            leaf.style().width(16f).height(16f);
            row.addChild(leaf);
            placed++;
            if (placed % COLUMNS == 0 && i + 1 < leaves) {
                row = new LayoutNode();
                row.style().direction(LayoutStyle.Direction.ROW);
                root.addChild(row);
            }
        }
        return root;
    }
}