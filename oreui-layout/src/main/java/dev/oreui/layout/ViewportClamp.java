package dev.oreui.layout;

import dev.oreui.core.geom.Insets;
import dev.oreui.core.geom.Rect2f;
import dev.oreui.core.geom.Size;

public final class ViewportClamp {
    private ViewportClamp() {
    }

    public static void clampPopup(Rect2f popup, Rect2f viewport) {
        if (popup.width() > viewport.width()) {
            popup.set(popup.x(), popup.y(), viewport.width(), popup.height());
        }
        if (popup.height() > viewport.height()) {
            popup.set(popup.x(), popup.y(), popup.width(), viewport.height());
        }
        if (popup.x() < viewport.x()) {
            popup.offset(viewport.x() - popup.x(), 0f);
        }
        if (popup.y() < viewport.y()) {
            popup.offset(0f, viewport.y() - popup.y());
        }
        if (popup.right() > viewport.right()) {
            popup.offset(viewport.right() - popup.right(), 0f);
        }
        if (popup.bottom() > viewport.bottom()) {
            popup.offset(0f, viewport.bottom() - popup.bottom());
        }
    }

    public static Rect2f placeAroundCursor(Size size, float cursorX, float cursorY, Insets cursorInset) {
        Rect2f popup = new Rect2f();
        placeAroundCursor(size, cursorX, cursorY, cursorInset, popup);
        return popup;
    }

    public static void placeAroundCursor(Size size, float cursorX, float cursorY,
                                         Insets cursorInset, Rect2f out) {
        out.set(cursorX + cursorInset.left(), cursorY + cursorInset.top(), size.width(), size.height());
    }
}