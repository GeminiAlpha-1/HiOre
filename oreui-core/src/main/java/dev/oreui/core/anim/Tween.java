package dev.oreui.core.anim;

public final class Tween {
    private final Easing easing;
    private final float duration;
    private float start;
    private float target;
    private float current;
    private float elapsed;

    public Tween(float initial, float duration, Easing easing) {
        if (duration < 0f) {
            throw new IllegalArgumentException("duration must be non-negative");
        }
        if (easing == null) {
            throw new NullPointerException("easing");
        }
        this.easing = easing;
        this.duration = duration;
        this.start = initial;
        this.target = initial;
        this.current = initial;
        this.elapsed = duration;
    }

    public void setTarget(float target) {
        this.start = current;
        this.target = target;
        this.elapsed = 0f;
        if (duration == 0f) {
            this.current = target;
        }
    }

    public void advance(float dt) {
        if (elapsed >= duration) {
            current = target;
            return;
        }
        if (dt <= 0f) {
            return;
        }
        elapsed += dt;
        if (elapsed >= duration) {
            elapsed = duration;
            current = target;
            return;
        }
        current = start + (target - start) * easing.apply(elapsed / duration);
    }

    public void snapToTarget() {
        current = target;
        elapsed = duration;
    }

    public float value() {
        return current;
    }

    public float target() {
        return target;
    }

    public boolean isFinished() {
        return elapsed >= duration;
    }
}