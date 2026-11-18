package dev.oreui.consumertest;

import dev.oreui.api.OreCanvas;
import dev.oreui.api.OreScene;
import dev.oreui.api.OreUI;
import dev.oreui.api.SceneSize;

/**
 * Compile-time consumer: assembles a control tree through the dev.oreui.api facade only.
 */
public final class ConsumerSmoke {
    private ConsumerSmoke() {
    }

    public static void main(String[] args) {
        OreScene scene = OreCanvas.create(SceneSize.MEDIUM);
        scene.label("consumer");
        scene.button("Go", true, () -> {
        });
        scene.button("Cancel", false, () -> {
        });
        scene.itemSlot(1, true, 30, 100);
        scene.itemGrid(9, 5, 12, 32);

        scene.layout();
        scene.update(1f / 20f);
        scene.render();

        System.out.println(OreUI.MOD_ID + ":" + OreUI.VERSION + " widgets=" + scene.widgetCount());
    }
}