package dev.oreui.components;

import dev.oreui.components.platform.IInputEvent;
import dev.oreui.components.platform.InputEvent;
import dev.oreui.components.platform.IText;
import dev.oreui.components.platform.IRenderContext;
import dev.oreui.core.geom.Rect2f;
import dev.oreui.core.geom.Size;
import dev.oreui.layout.ViewportClamp;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Phase7OverlayTest {
    @Test
    void dropdownPopupUsesOverlayManager() {
        ComponentContext context = TestFixtures.context();
        OreDropdown dropdown = new OreDropdown(context, DropdownSize.STANDARD,
                List.of(TestFixtures.text("A"), TestFixtures.text("B")));
        dropdown.style().width(100f);
        dropdown.layoutNode().frame().set(0f, 0f, 100f, 20f);
        OreSurface root = new OreSurface(context, SurfaceVariant.PANEL);
        TestFixtures.fillSurface(context, root, 200, 200);
        root.addChild(dropdown);
        ComponentTree tree = new ComponentTree(root);
        tree.layout(new Rect2f(0f, 0f, 200f, 200f));
        dropdown.setOpen(true);
        dropdown.syncFrames();
        assertEquals(1, tree.overlayManager().overlays().size());
        assertTrue(tree.overlayManager().dispatch(new InputEvent(IInputEvent.Kind.POINTER_DOWN, 10f, 25f, 0, 0, '\0')));
        assertFalse(dropdown.isOpen());
        assertEquals(0, dropdown.selected());
        assertEquals(0, tree.overlayManager().overlays().size());
    }

    @Test
    void tooltipPlacementsAndViewportClamp() {
        ComponentContext context = TestFixtures.context();
        OreTooltip tooltip = new OreTooltip(context, TestFixtures.text("tip"), TooltipPlacement.BOTTOM);
        tooltip.layoutNode().frame().set(0f, 0f, 180f, 26f);
        tooltip.syncFrames();
        Rect2f viewport = new Rect2f(0f, 0f, 200f, 200f);
        tooltip.place(new Rect2f(10f, 10f, 20f, 10f), viewport);
        assertEquals(24f, tooltip.popup().y(), 0.1f);
        tooltip.place(new Rect2f(190f, 190f, 20f, 10f), viewport);
        assertTrue(tooltip.popup().right() <= 200f);
        assertTrue(tooltip.popup().bottom() <= 200f);
        assertEquals(ViewportClamp.placeAroundCursor(new Size(10, 10), 0, 0, new dev.oreui.core.geom.Insets()).x(), 0f, 0.1f);
    }

    @Test
    void modalViewportBackdropAndFocusLock() {
        ComponentContext context = TestFixtures.context();
        OreModal modal = new OreModal(context, TestFixtures.text("Title"), TestFixtures.text("Body"),
                ModalSize.DIALOG_MD);
        modal.setViewport(new Rect2f(0f, 0f, 300f, 200f));
        modal.setOpen(true);
        assertTrue(modal.isFocusLocked());
        modal.pointerDown(10f, 10f);
        assertFalse(modal.isOpen());
        assertFalse(modal.isFocusLocked());
        RecordingContext recording = new RecordingContext();
        modal.setOpen(true);
        modal.renderBackground(recording);
        assertEquals(300f, recording.firstWidth, 0.1f);
        assertEquals(200f, recording.firstHeight, 0.1f);
    }

    private static final class RecordingContext implements IRenderContext {
        private float firstWidth;
        private float firstHeight;

        @Override public void begin() { }
        @Override public void end() { }
        @Override public void submitPixelRect(Rect2f rect, int argb) {
            if (firstWidth == 0f) {
                firstWidth = rect.width();
                firstHeight = rect.height();
            }
        }
        @Override public void submitSdfRect(Rect2f rect, int argb, float radius, float border) { }
        @Override public void submitText(IText text, float x, float y, dev.oreui.theme.TypeRole role,
                                         dev.oreui.theme.ColorRole color) { }
        @Override public void pushClip(Rect2f rect) { }
        @Override public void popClip() { }
        @Override public void pushTransform(float x, float y, float scale) { }
        @Override public void popTransform() { }
        @Override public void pushOpacity(float alpha) { }
        @Override public void popOpacity() { }
    }
}