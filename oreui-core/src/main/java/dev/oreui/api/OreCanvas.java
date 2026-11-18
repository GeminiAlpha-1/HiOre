package dev.oreui.api;

import java.util.ServiceLoader;

public final class OreCanvas {
    private OreCanvas() {
    }

    public static OreScene create(SceneSize size) {
        return ServiceLoader.load(OreSceneFactory.class).findFirst()
                .orElseThrow(() -> new IllegalStateException("No OreSceneFactory on the classpath"))
                .create(size);
    }
}