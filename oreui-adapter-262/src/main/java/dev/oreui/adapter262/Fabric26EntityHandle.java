package dev.oreui.adapter262;

import dev.oreui.components.platform.IGameObjectRenderer.IEntityHandle;
import net.minecraft.world.entity.Entity;

public final class Fabric26EntityHandle implements IEntityHandle {
    private final Entity entity;

    public Fabric26EntityHandle(Entity entity) {
        this.entity = entity;
    }

    public Entity entity() {
        return entity;
    }
}