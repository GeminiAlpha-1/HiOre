package dev.oreui.api;

public interface OreScene {
    OreScene label(String text);

    OreScene button(String text, boolean primary, Runnable action);

    OreScene toggle(boolean on);

    OreScene slider(float value);

    OreScene textField(String text);

    OreScene accordion(String title, String body);

    OreScene itemSlot(int count, boolean damageable, int durability, int durabilityMax);

    OreScene itemGrid(int columns, int... counts);

    void layout();

    void update(float deltaSeconds);

    void render();

    int widgetCount();
}