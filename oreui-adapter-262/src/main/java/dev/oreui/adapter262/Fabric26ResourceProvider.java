package dev.oreui.adapter262;

import dev.oreui.components.platform.IResourceProvider;
import dev.oreui.core.geom.Insets;
import dev.oreui.theme.AssetKey;
import dev.oreui.theme.OreTheme;

public final class Fabric26ResourceProvider implements IResourceProvider {
    private final OreTheme theme;

    public Fabric26ResourceProvider(OreTheme theme) {
        this.theme = theme;
    }

    @Override
    public SpriteInfo sprite(AssetKey key, int width, int height) {
        return new SpriteInfo(theme.asset(key), width, height);
    }

    @Override
    public NineSliceInfo nineSlice(AssetKey key, Insets insets) {
        return new NineSliceInfo(theme.asset(key), insets);
    }
}