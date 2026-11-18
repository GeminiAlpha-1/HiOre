package dev.oreui.core.geom;

public final class Rect2f {
    private float x;
    private float y;
    private float width;
    private float height;

    public Rect2f() {
    }

    public Rect2f(float x, float y, float width, float height) {
        set(x, y, width, height);
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public float width() {
        return width;
    }

    public float height() {
        return height;
    }

    public float right() {
        return x + width;
    }

    public float bottom() {
        return y + height;
    }

    public Rect2f set(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        return this;
    }

    public Rect2f set(Rect2f other) {
        return set(other.x, other.y, other.width, other.height);
    }

    public Rect2f offset(float dx, float dy) {
        x += dx;
        y += dy;
        return this;
    }

    public Rect2f deflate(Insets insets) {
        x += insets.left();
        y += insets.top();
        width -= insets.left() + insets.right();
        height -= insets.top() + insets.bottom();
        if (width < 0f) {
            width = 0f;
        }
        if (height < 0f) {
            height = 0f;
        }
        return this;
    }

    public boolean contains(float px, float py) {
        return !isEmpty() && px >= x && px < right() && py >= y && py < bottom();
    }

    public boolean intersects(Rect2f other) {
        return !isEmpty() && !other.isEmpty()
                && x < other.right() && other.x < right()
                && y < other.bottom() && other.y < bottom();
    }

    public boolean intersect(Rect2f other) {
        if (!intersects(other)) {
            x = Math.max(x, other.x);
            y = Math.max(y, other.y);
            width = 0f;
            height = 0f;
            return false;
        }
        float nextX = Math.max(x, other.x);
        float nextY = Math.max(y, other.y);
        float nextRight = Math.min(right(), other.right());
        float nextBottom = Math.min(bottom(), other.bottom());
        x = nextX;
        y = nextY;
        width = nextRight - nextX;
        height = nextBottom - nextY;
        return true;
    }

    public boolean isEmpty() {
        return width <= 0f || height <= 0f;
    }

    public float area() {
        return isEmpty() ? 0f : width * height;
    }
}