package dev.oreui.adapter262.fixture;

import dev.oreui.components.internal.InternalRenderBridge;

/**
 * Negative fixture: adapter262 must not call components.internal directly.
 */
public final class AdapterInternalLeak {
    private AdapterInternalLeak() {
    }

    public static Class<?> internalType() {
        return InternalRenderBridge.class;
    }
}