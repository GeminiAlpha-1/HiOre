package dev.oreui.core.anim;

@FunctionalInterface
public interface Easing {
    float apply(float t);
}