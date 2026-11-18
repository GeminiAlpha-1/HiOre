package dev.oreui.components.platform;

import dev.oreui.core.geom.Insets;
import dev.oreui.theme.AssetKey;

public interface IResourceProvider {
    SpriteInfo sprite(AssetKey key, int width, int height);

    NineSliceInfo nineSlice(AssetKey key, Insets insets);

    record SpriteInfo(String id, int width, int height) {
        public SpriteInfo {
            if (id == null) {
                throw new NullPointerException("id");
            }
            if (width < 0 || height < 0) {
                throw new IllegalArgumentException("sprite dimensions must be non-negative");
            }
        }
    }

    record NineSliceInfo(String id, Insets insets) {
        public NineSliceInfo {
            if (id == null || insets == null) {
                throw new NullPointerException("id/insets");
            }
        }
    }
}