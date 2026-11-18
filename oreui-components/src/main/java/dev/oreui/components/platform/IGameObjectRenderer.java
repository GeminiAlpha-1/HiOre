package dev.oreui.components.platform;

import dev.oreui.core.geom.Rect2f;

public interface IGameObjectRenderer {
    void submitItem(IItemHandle handle, Rect2f rect, float scale);

    void submitFluid(IFluidHandle handle, Rect2f rect);

    void submitEntity(IEntityHandle handle, Rect2f rect, float yaw, float pitch);

    interface IItemHandle {
        boolean isEmpty();

        int count();

        boolean isDamageable();

        int durability();

        int durabilityMax();
    }

    interface IFluidHandle {
    }

    interface IEntityHandle {
    }
}