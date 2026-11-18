package dev.oreui.core.fixture;

import java.awt.Rectangle;

/**
 * Negative fixture: core must not depend on AWT.
 */
public final class CoreAwtLeak {
    private CoreAwtLeak() {
    }

    public static int area(Rectangle rectangle) {
        return rectangle.width * rectangle.height;
    }
}