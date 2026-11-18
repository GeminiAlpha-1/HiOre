package dev.oreui.components;

import dev.oreui.api.OreScene;
import dev.oreui.api.OreSceneFactory;
import dev.oreui.api.SceneSize;
import dev.oreui.theme.SizeToken;

public final class ApiSceneFactory implements OreSceneFactory {
    @Override
    public OreScene create(SceneSize size) {
        return switch (size) {
            case MEDIUM -> new ApiScene(SizeToken.MODAL_W_MD, SizeToken.MODAL_W_SM);
            case LARGE -> new ApiScene(SizeToken.MODAL_W_MD, SizeToken.TOOLTIP_MAX_WIDTH);
        };
    }
}