package dev.oreui.components;

import dev.oreui.components.platform.IRenderContext;
import dev.oreui.components.platform.IText;
import dev.oreui.theme.ColorRole;
import dev.oreui.theme.TypeRole;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Phase10ControlsTest {
    @Test
    void segmentedControlSelectsSegment() {
        OreSegmentedControl control = new OreSegmentedControl(TestFixtures.context(),
                Orientation.HORIZONTAL, List.of(new LiteralText("A"), new LiteralText("B"),
                        new LiteralText("C")));
        control.layoutNode().frame().set(0f, 0f, 90f, 22f);
        control.syncFrames();
        assertTrue(control.pointerDown(75f, 10f));
        assertEquals(2, control.selected());
    }

    @Test
    void accordionSingleModeCollapsesPreviousSection() {
        OreAccordion accordion = new OreAccordion(TestFixtures.context(), AccordionMode.SINGLE);
        accordion.addSection(new LiteralText("One"), new OreSurface(TestFixtures.context(), SurfaceVariant.CARD));
        accordion.addSection(new LiteralText("Two"), new OreSurface(TestFixtures.context(), SurfaceVariant.CARD));
        accordion.layoutNode().frame().set(0f, 0f, 180f, 100f);
        accordion.syncFrames();
        accordion.setExpanded(0, true);
        assertTrue(accordion.isExpanded(0));
        accordion.setExpanded(1, true);
        assertTrue(accordion.isExpanded(1));
        assertFalse(accordion.isExpanded(0));
    }

    @Test
    void accordionMultiModeKeepsSectionsOpen() {
        OreAccordion accordion = new OreAccordion(TestFixtures.context(), AccordionMode.MULTI);
        accordion.addSection(new LiteralText("One"), new OreSurface(TestFixtures.context(), SurfaceVariant.CARD));
        accordion.addSection(new LiteralText("Two"), new OreSurface(TestFixtures.context(), SurfaceVariant.CARD));
        accordion.setExpanded(0, true);
        accordion.setExpanded(1, true);
        assertTrue(accordion.isExpanded(0));
        assertTrue(accordion.isExpanded(1));
    }

    @Test
    void virtualListInstantiatesVisibleWindowOnly() {
        OreVirtualList<String> list = virtualList(60f);
        assertEquals(5, list.instantiatedRows());
        list.setScroll(200f);
        assertEquals(9, list.firstVisibleIndex());
        assertTrue(list.instantiatedRows() <= 5);
    }

    @Test
    void contextMenuOpensAndClosesThroughOverlay() {
        ComponentContext context = TestFixtures.context();
        OreSurface root = new OreSurface(context, SurfaceVariant.PANEL);
        OreContextMenu menu = new OreContextMenu(context, List.of(
                new ContextMenuEntry(new LiteralText("Rename"), false, () -> {
                }),
                new ContextMenuEntry(new LiteralText("Delete"), true, () -> {
                })));
        root.addChild(menu);
        ComponentTree tree = new ComponentTree(root);
        menu.setOverlayManager(tree.overlayManager());
        menu.open(10f, 10f);
        assertTrue(menu.isOpen());
        assertEquals(1, tree.overlayManager().overlays().size());
        menu.close();
        assertFalse(menu.isOpen());
        assertEquals(0, tree.overlayManager().overlays().size());
    }

    @Test
    void keybindHonoursConflictPolicy() {
        OreKeybind warn = new OreKeybind(TestFixtures.context(), KeyConflictPolicy.WARN);
        warn.setConflict(true);
        assertTrue(warn.isConflict());
        OreKeybind allow = new OreKeybind(TestFixtures.context(), KeyConflictPolicy.ALLOW);
        allow.setConflict(true);
        assertFalse(allow.isConflict());
    }

    @Test
    void keybindCapturesNextKey() {
        OreKeybind keybind = new OreKeybind(TestFixtures.context(), KeyConflictPolicy.WARN);
        keybind.layoutNode().frame().set(0f, 0f, 60f, 20f);
        keybind.syncFrames();
        assertTrue(keybind.pointerDown(10f, 10f));
        assertTrue(keybind.isCapturing());
        assertTrue(keybind.keyPressed(KeyCodes.ENTER, 0));
        assertFalse(keybind.isCapturing());
        assertEquals(KeyCodes.ENTER, keybind.keyCode());
    }

    @Test
    void fluidSlotClampsLevel() {
        OreFluidSlot slot = new OreFluidSlot(TestFixtures.context());
        slot.setLevel(1.5f);
        assertEquals(1f, slot.level(), 0.001f);
        slot.setLevel(-0.5f);
        assertEquals(0f, slot.level(), 0.001f);
        slot.layoutNode().frame().set(0f, 0f, 18f, 18f);
        slot.syncFrames();
        slot.renderBackground(new NullRenderContext());
    }

    @Test
    void entityPreviewDampsTowardsTarget() {
        OreEntityPreview preview = new OreEntityPreview(TestFixtures.context());
        preview.layoutNode().frame().set(0f, 0f, 40f, 40f);
        preview.syncFrames();
        preview.setView(0f, 0f);
        assertTrue(preview.pointerDown(10f, 10f));
        assertTrue(preview.pointerMove(20f, 10f));
        preview.update(0.05f);
        assertEquals(0.75f, preview.yaw(), 0.001f);
        assertTrue(preview.pointerUp(20f, 10f));
        assertFalse(preview.isDragging());
    }

    @Test
    void panelFrameClosesFromCloseButton() {
        boolean[] closed = {false};
        OrePanelFrame frame = new OrePanelFrame(TestFixtures.context(), new LiteralText("Panel"));
        frame.setOnClose(() -> closed[0] = true);
        frame.layoutNode().frame().set(0f, 0f, 260f, 160f);
        frame.syncFrames();
        assertTrue(frame.pointerDown(frame.bounds().right() - 10f, 11f));
        assertTrue(closed[0]);
    }

    @Test
    void panelFrameDragsTitleBarOnly() {
        OrePanelFrame frame = new OrePanelFrame(TestFixtures.context(), new LiteralText("Panel"));
        frame.layoutNode().frame().set(20f, 20f, 260f, 160f);
        frame.syncFrames();
        assertTrue(frame.pointerDown(60f, 28f));
        assertTrue(frame.isDragging());
        assertTrue(frame.pointerMove(100f, 60f));
        assertEquals(60f, frame.bounds().x(), 0.01f);
        assertEquals(52f, frame.bounds().y(), 0.01f);
        assertTrue(frame.pointerUp(100f, 60f));
        assertFalse(frame.isDragging());
    }

    private static OreVirtualList<String> virtualList(float height) {
        String[] values = new String[100];
        for (int i = 0; i < values.length; i++) {
            values[i] = "Row " + i;
        }
        OreVirtualList<String> list = new OreVirtualList<>(TestFixtures.context(),
                RowHeightPolicy.FIXED, 20f, List.of(values),
                (context, value) -> new OreLabel(context, new LiteralText(value),
                        TypeRole.CAPTION, LabelOverflow.CLIP, 100));
        list.style().height(height);
        list.layoutNode().frame().set(0f, 0f, 160f, height);
        list.syncFrames();
        return list;
    }

    private static final class NullRenderContext implements IRenderContext {
        @Override
        public void begin() {
        }

        @Override
        public void end() {
        }

        @Override
        public void submitPixelRect(dev.oreui.core.geom.Rect2f rect, int argb) {
        }

        @Override
        public void submitSdfRect(dev.oreui.core.geom.Rect2f rect, int argb, float radius, float border) {
        }

        @Override
        public void submitText(IText text, float x, float y, TypeRole role, ColorRole color) {
        }

        @Override
        public void pushClip(dev.oreui.core.geom.Rect2f rect) {
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