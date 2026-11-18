package dev.oreui.components;

import dev.oreui.components.platform.IGameObjectRenderer;
import dev.oreui.components.platform.IGameObjectRenderer.IItemHandle;
import dev.oreui.components.platform.IRenderContext;
import dev.oreui.components.platform.IText;
import dev.oreui.core.geom.Rect2f;
import dev.oreui.theme.ColorRole;
import dev.oreui.theme.TypeRole;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Phase8ItemsTest {
    @Test
    void slotSnapshotsItemState() {
        ComponentContext context = TestFixtures.context();
        OreItemSlot slot = new OreItemSlot(context, SlotStyle.STANDARD_18);
        slot.setItem(new FakeItem(false, 5, true, 80, 100));
        slot.update(0.016f);
        assertEquals(5, slot.count());
        assertSame(SlotStyle.STANDARD_18, slot.slotStyle());
        assertEquals(18f, slot.style().width(), 0.01f);
        assertEquals(18f, slot.style().height(), 0.01f);
    }

    @Test
    void emptySlotShowsNoCount() {
        ComponentContext context = TestFixtures.context();
        OreItemSlot slot = new OreItemSlot(context, SlotStyle.STANDARD_18);
        FakeItem empty = new FakeItem(true, 0, false, 0, 0);
        slot.setItem(empty);
        slot.update(0.016f);
        assertEquals(0, slot.count());
        slot.layoutNode().frame().set(0f, 0f, 18f, 18f);
        slot.syncFrames();
        slot.renderBackground(new RecordingContext());
        slot.renderForeground(new RecordingContext());
    }

    @Test
    void slotSubmitsItemIntoInnerRect() {
        RecordingRenderer renderer = new RecordingRenderer();
        ComponentContext context = TestFixtures.context(renderer);
        OreItemSlot slot = new OreItemSlot(context, SlotStyle.LARGE_26);
        FakeItem item = new FakeItem(false, 3, false, 0, 0);
        slot.setItem(item);
        slot.layoutNode().frame().set(10f, 20f, 26f, 26f);
        slot.syncFrames();
        slot.update(0.016f);
        slot.renderBackground(new RecordingContext());
        assertEquals(1, renderer.submissions);
        assertSame(item, renderer.last);
        assertEquals(13f, renderer.lastRect.x(), 0.01f);
        assertEquals(23f, renderer.lastRect.y(), 0.01f);
        assertEquals(20f, renderer.lastRect.width(), 0.01f);
        assertEquals(20f / 16f, renderer.lastScale, 0.001f);
    }

    @Test
    void gridBuildsRowsByColumns() {
        ComponentContext context = TestFixtures.context();
        OreItemGrid grid = new OreItemGrid(context);
        grid.setItems(items(20));
        assertEquals(9, grid.columns());
        assertEquals(20, grid.slots().size());
        assertEquals(3, grid.children().size());
        assertEquals(9 * 18f, grid.style().width(), 0.01f);
        assertEquals(3 * 18f, grid.style().height(), 0.01f);
    }

    @Test
    void gridReplacesPreviousRows() {
        ComponentContext context = TestFixtures.context();
        OreItemGrid grid = new OreItemGrid(context, 4);
        grid.setItems(items(4));
        assertEquals(1, grid.children().size());
        OreWidget firstRow = grid.children().get(0);
        grid.setItems(items(10));
        assertEquals(3, grid.children().size());
        assertEquals(10, grid.slots().size());
        assertFalse(grid.children().contains(firstRow));
    }

    @Test
    void gridRejectsInvalidColumns() {
        ComponentContext context = TestFixtures.context();
        boolean rejected = false;
        try {
            new OreItemGrid(context, 0);
        } catch (IllegalArgumentException expected) {
            rejected = true;
        }
        assertTrue(rejected);
    }

    private static List<IItemHandle> items(int count) {
        List<IItemHandle> items = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            items.add(new FakeItem(false, 1, false, 0, 0));
        }
        return items;
    }

    private static final class FakeItem implements IItemHandle {
        private boolean empty;
        private int count;
        private boolean damageable;
        private int durability;
        private int durabilityMax;

        private FakeItem(boolean empty, int count, boolean damageable, int durability, int durabilityMax) {
            this.empty = empty;
            this.count = count;
            this.damageable = damageable;
            this.durability = durability;
            this.durabilityMax = durabilityMax;
        }

        @Override
        public boolean isEmpty() {
            return empty;
        }

        @Override
        public int count() {
            return count;
        }

        @Override
        public boolean isDamageable() {
            return damageable;
        }

        @Override
        public int durability() {
            return durability;
        }

        @Override
        public int durabilityMax() {
            return durabilityMax;
        }
    }

    private static final class RecordingRenderer implements IGameObjectRenderer {
        private int submissions;
        private IItemHandle last;
        private float lastScale;
        private final Rect2f lastRect = new Rect2f();

        @Override
        public void submitItem(IItemHandle handle, Rect2f rect, float scale) {
            submissions++;
            last = handle;
            lastScale = scale;
            lastRect.set(rect);
        }

        @Override
        public void submitFluid(IFluidHandle handle, Rect2f rect) {
            throw new UnsupportedOperationException("not used in item tests");
        }

        @Override
        public void submitEntity(IEntityHandle handle, Rect2f rect, float yaw, float pitch) {
            throw new UnsupportedOperationException("not used in item tests");
        }
    }

    private static final class RecordingContext implements IRenderContext {
        private int fills;
        private int texts;

        @Override
        public void begin() {
        }

        @Override
        public void end() {
        }

        @Override
        public void submitPixelRect(Rect2f rect, int argb) {
            fills++;
        }

        @Override
        public void submitSdfRect(Rect2f rect, int argb, float radius, float border) {
            fills++;
        }

        @Override
        public void submitText(IText text, float x, float y, TypeRole role, ColorRole color) {
            texts++;
        }

        @Override
        public void pushClip(Rect2f rect) {
        }

        @Override
        public void popClip() {
        }

        @Override
        public void pushTransform(float translateX, float translateY, float scale) {
        }

        @Override
        public void popTransform() {
        }

        @Override
        public void pushOpacity(float alpha) {
        }

        @Override
        public void popOpacity() {
        }
    }
}