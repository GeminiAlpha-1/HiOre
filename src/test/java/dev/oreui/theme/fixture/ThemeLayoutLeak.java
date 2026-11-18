package dev.oreui.theme.fixture;

import dev.oreui.layout.LayoutModule;

/**
 * Negative fixture: theme must not depend on layout.
 */
public final class ThemeLayoutLeak {
    private ThemeLayoutLeak() {
    }

    public static Class<?> layoutType() {
        return LayoutModule.class;
    }
}