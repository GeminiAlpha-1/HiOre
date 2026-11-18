package dev.oreui.adapter262;

import dev.oreui.components.platform.IGameObjectRenderer.IItemHandle;
import net.minecraft.world.item.ItemStack;

public final class Fabric26ItemHandle implements IItemHandle {
    private ItemStack stack;

    public Fabric26ItemHandle(ItemStack stack) {
        this.stack = stack == null ? ItemStack.EMPTY : stack;
    }

    public ItemStack stack() {
        return stack;
    }

    public void setStack(ItemStack stack) {
        this.stack = stack == null ? ItemStack.EMPTY : stack;
    }

    @Override
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    @Override
    public int count() {
        return stack.getCount();
    }

    @Override
    public boolean isDamageable() {
        return stack.isDamageableItem();
    }

    @Override
    public int durability() {
        int max = stack.getMaxDamage();
        return max <= 0 ? 0 : max - stack.getDamageValue();
    }

    @Override
    public int durabilityMax() {
        return Math.max(0, stack.getMaxDamage());
    }
}