package dev.oreui.adapter262;

final class RenderStack {
    static final int MAX_DEPTH = 8;

    private final float[] opacityStack = new float[MAX_DEPTH];
    private int transformDepth;
    private int clipDepth;
    private int opacityDepth;
    private float opacity = 1f;

    void begin() {
        transformDepth = 1;
        clipDepth = 0;
        opacityDepth = 0;
        opacity = 1f;
    }

    void end() {
        transformDepth = 0;
        clipDepth = 0;
        opacityDepth = 0;
        opacity = 1f;
    }

    int transformDepth() {
        return transformDepth;
    }

    int clipDepth() {
        return clipDepth;
    }

    float opacity() {
        return opacity;
    }

    void pushTransform() {
        transformDepth++;
    }

    void popTransform() {
        if (transformDepth <= 1) {
            throw new IllegalStateException("transform stack underflow");
        }
        transformDepth--;
    }

    void pushClip() {
        if (clipDepth >= MAX_DEPTH) {
            throw new IllegalStateException("clip stack overflow");
        }
        clipDepth++;
    }

    void popClip() {
        if (clipDepth <= 0) {
            throw new IllegalStateException("clip stack underflow");
        }
        clipDepth--;
    }

    void pushOpacity(float alpha) {
        if (opacityDepth >= MAX_DEPTH) {
            throw new IllegalStateException("opacity stack overflow");
        }
        opacityStack[opacityDepth++] = opacity;
        opacity = Math.max(0f, Math.min(1f, opacity * alpha));
    }

    void popOpacity() {
        if (opacityDepth <= 0) {
            throw new IllegalStateException("opacity stack underflow");
        }
        opacity = opacityStack[--opacityDepth];
    }
}