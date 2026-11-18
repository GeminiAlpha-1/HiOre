package dev.oreui.components;

public interface VirtualRowFactory<T> {
    OreWidget create(ComponentContext context, T value);
}