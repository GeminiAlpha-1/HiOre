package dev.oreui.layout;

import dev.oreui.core.geom.Insets;
import dev.oreui.core.geom.Rect2f;
import dev.oreui.core.geom.Size;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ViewportClampTest {
    @Test
    void insidePopupIsUnchanged() {
        Rect2f popup = new Rect2f(10f, 10f, 30f, 20f);
        ViewportClamp.clampPopup(popup, new Rect2f(0f, 0f, 100f, 100f));
        assertEquals(10f, popup.x(), 0.0001f);
        assertEquals(10f, popup.y(), 0.0001f);
        assertEquals(30f, popup.width(), 0.0001f);
        assertEquals(20f, popup.height(), 0.0001f);
    }

    @Test
    void rightAndBottomAreClamped() {
        Rect2f popup = new Rect2f(90f, 90f, 30f, 30f);
        ViewportClamp.clampPopup(popup, new Rect2f(0f, 0f, 100f, 100f));
        assertEquals(70f, popup.x(), 0.0001f);
        assertEquals(70f, popup.y(), 0.0001f);
    }

    @Test
    void leftAndTopAreClamped() {
        Rect2f popup = new Rect2f(-10f, -10f, 20f, 20f);
        ViewportClamp.clampPopup(popup, new Rect2f(0f, 0f, 100f, 100f));
        assertEquals(0f, popup.x(), 0.0001f);
        assertEquals(0f, popup.y(), 0.0001f);
    }

    @Test
    void oversizedPopupIsShrunk() {
        Rect2f popup = new Rect2f(10f, 10f, 150f, 150f);
        ViewportClamp.clampPopup(popup, new Rect2f(0f, 0f, 100f, 80f));
        assertEquals(0f, popup.x(), 0.0001f);
        assertEquals(0f, popup.y(), 0.0001f);
        assertEquals(100f, popup.width(), 0.0001f);
        assertEquals(80f, popup.height(), 0.0001f);
    }

    @Test
    void placesAroundCursorWithInset() {
        Rect2f popup = ViewportClamp.placeAroundCursor(new Size(40f, 20f), 10f, 10f,
                new Insets(1f, 2f, 3f, 4f));
        assertEquals(14f, popup.x(), 0.0001f);
        assertEquals(11f, popup.y(), 0.0001f);
        assertEquals(40f, popup.width(), 0.0001f);
        assertEquals(20f, popup.height(), 0.0001f);
    }

    @Test
    void placeAroundCursorReusesOutput() {
        Rect2f out = new Rect2f();
        ViewportClamp.placeAroundCursor(new Size(10f, 10f), 1f, 2f, new Insets(), out);
        assertEquals(1f, out.x(), 0.0001f);
        assertEquals(2f, out.y(), 0.0001f);
    }
}