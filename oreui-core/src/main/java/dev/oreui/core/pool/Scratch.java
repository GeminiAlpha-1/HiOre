package dev.oreui.core.pool;

import java.util.function.Supplier;

public final class Scratch<T> {
    private final Supplier<T> factory;
    private T value;
    private boolean initialized;

    public Scratch(Supplier<T> factory) {
        if (factory == null) {
            throw new NullPointerException("factory");
        }
        this.factory = factory;
    }

    public T get() {
        if (!initialized) {
            value = factory.get();
            initialized = true;
        }
        return value;
    }

    public void reset() {
        value = factory.get();
        initialized = true;
    }

    public boolean isInitialized() {
        return initialized;
    }
}