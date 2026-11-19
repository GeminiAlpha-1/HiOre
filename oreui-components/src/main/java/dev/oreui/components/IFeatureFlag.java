package dev.oreui.components;

@FunctionalInterface
public interface IFeatureFlag {
    boolean isEnabled(String feature);
}