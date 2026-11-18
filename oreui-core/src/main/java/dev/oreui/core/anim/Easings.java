package dev.oreui.core.anim;

public final class Easings {
    public static final Easing ORE_STANDARD = cubicBezier(0.2f, 0f, 0f, 1f);
    public static final Easing ORE_ENTER = cubicBezier(0.22f, 1f, 0.36f, 1f);
    public static final Easing ORE_EXIT = cubicBezier(0.4f, 0f, 1f, 1f);

    private Easings() {
    }

    public static Easing cubicBezier(float x1, float y1, float x2, float y2) {
        validate(x1);
        validate(y1);
        validate(x2);
        validate(y2);
        return new CubicBezier(x1, y1, x2, y2);
    }

    private static void validate(float value) {
        if (!(value >= 0f && value <= 1f)) {
            throw new IllegalArgumentException("cubic-bezier control points must be within [0, 1]");
        }
    }

    private static final class CubicBezier implements Easing {
        private final float x1;
        private final float y1;
        private final float x2;
        private final float y2;

        private CubicBezier(float x1, float y1, float x2, float y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        @Override
        public float apply(float t) {
            float clamped = t < 0f ? 0f : (t > 1f ? 1f : t);
            if (clamped == 0f || clamped == 1f) {
                return clamped;
            }
            return bezier(solve(clamped), y1, y2);
        }

        private float solve(float x) {
            float low = 0f;
            float high = 1f;
            for (int i = 0; i < 20; i++) {
                float middle = (low + high) * 0.5f;
                if (bezier(middle, x1, x2) < x) {
                    low = middle;
                } else {
                    high = middle;
                }
            }
            return (low + high) * 0.5f;
        }

        private static float bezier(float t, float p1, float p2) {
            float inverse = 1f - t;
            return 3f * inverse * inverse * t * p1 + 3f * inverse * t * t * p2 + t * t * t;
        }
    }
}