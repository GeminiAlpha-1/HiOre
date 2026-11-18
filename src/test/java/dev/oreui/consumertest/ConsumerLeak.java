package dev.oreui.consumertest;

import dev.oreui.layout.LayoutModule;

/**
 * Negative fixture: consumer-test must only touch dev.oreui.api.
 */
public final class ConsumerLeak {
    private ConsumerLeak() {
    }

    public static Class<?> nonApiType() {
        return LayoutModule.class;
    }
}