package dev.oreui.adapter262;

import dev.oreui.components.platform.IResourceProvider;
import dev.oreui.core.geom.Insets;
import dev.oreui.theme.AssetKey;
import dev.oreui.theme.OreTokens;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Fabric26ResourceProviderTest {
    @Test
    void mapsAssetKeysThroughTheme() {
        Fabric26ResourceProvider provider = new Fabric26ResourceProvider(OreTokens.DARK);
        IResourceProvider.SpriteInfo sprite = provider.sprite(AssetKey.SHADER_SDF_UI, 32, 16);
        assertEquals("oreui:sdf_ui", sprite.id());
        assertEquals(32, sprite.width());
        assertEquals(16, sprite.height());

        IResourceProvider.NineSliceInfo slice = provider.nineSlice(AssetKey.PANEL_NINE_SLICE,
                new Insets(1f, 2f, 3f, 4f));
        assertEquals("oreui:panel_nine_slice", slice.id());
        assertEquals(4f, slice.insets().left(), 0.0001f);
    }
}