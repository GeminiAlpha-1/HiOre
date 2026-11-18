package dev.oreui.components.fixture;

import dev.oreui.components.internal.InternalRenderBridge;

/**
 * Negative fixture: code outside components.internal must not call it.
 */
public final class ComponentsInternalLeak {
    private ComponentsInternalLeak() {
    }

    public static Class<?> internalType() {
        return InternalRenderBridge.class;
    }
}