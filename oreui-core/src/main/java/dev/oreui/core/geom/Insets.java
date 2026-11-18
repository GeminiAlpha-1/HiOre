package dev.oreui.core.geom;

public final class Insets {
    private float top;
    private float right;
    private float bottom;
    private float left;

    public Insets() {
    }

    public Insets(float top, float right, float bottom, float left) {
        set(top, right, bottom, left);
    }

    public static Insets uniform(float value) {
        return new Insets(value, value, value, value);
    }

    public float top() {
        return top;
    }

    public float right() {
        return right;
    }

    public float bottom() {
        return bottom;
    }

    public float left() {
        return left;
    }

    public float horizontal() {
        return left + right;
    }

    public float vertical() {
        return top + bottom;
    }

    public Insets set(float top, float right, float bottom, float left) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
        return this;
    }

    public Insets set(Insets other) {
        return set(other.top, other.right, other.bottom, other.left);
    }
}