package dev.oreui.core.geom;

public final class Size {
    private float width;
    private float height;

    public Size() {
    }

    public Size(float width, float height) {
        set(width, height);
    }

    public float width() {
        return width;
    }

    public float height() {
        return height;
    }

    public Size set(float width, float height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public Size set(Size other) {
        return set(other.width, other.height);
    }

    public boolean isEmpty() {
        return width <= 0f || height <= 0f;
    }

    public float aspect() {
        return height == 0f ? 0f : width / height;
    }
}