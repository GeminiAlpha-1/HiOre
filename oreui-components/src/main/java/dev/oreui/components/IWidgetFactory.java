package dev.oreui.components;

@FunctionalInterface
public interface IWidgetFactory {
    OreWidget create(ComponentContext context);
}