package dev.oreui.api;

public interface OreScene {
    OreScene label(String text);

    OreScene button(String text, boolean primary, Runnable action);

    OreScene itemSlot(int count, boolean damageable, int durability, int durabilityMax);

    OreScene itemGrid(int columns, int... counts);

    void layout();

    void update(float deltaSeconds);

    void render();

    int widgetCount();
}