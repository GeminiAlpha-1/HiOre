package dev.oreui.adapter262;

import dev.oreui.components.platform.IGameObjectRenderer.IFluidHandle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;

public final class Fabric26FluidHandle implements IFluidHandle {
    private static final Identifier BLOCKS_ATLAS =
            Identifier.fromNamespaceAndPath("minecraft", "textures/atlas/blocks.png");

    private final Identifier stillTexture;
    private final int tint;
    private final SpriteId spriteId;
    private TextureAtlasSprite sprite;

    public Fabric26FluidHandle(Identifier stillTexture, int tint) {
        this.stillTexture = stillTexture;
        this.tint = tint;
        this.spriteId = new SpriteId(BLOCKS_ATLAS, stillTexture);
    }

    public Identifier stillTexture() {
        return stillTexture;
    }

    public int tint() {
        return tint;
    }

    public TextureAtlasSprite sprite() {
        if (sprite == null) {
            sprite = Minecraft.getInstance().getAtlasManager().get(spriteId);
        }
        return sprite;
    }
}