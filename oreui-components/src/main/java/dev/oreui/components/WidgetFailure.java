package dev.oreui.components;

public record WidgetFailure(OreWidget widget, String phase, Throwable cause) {
    public WidgetFailure {
        if (widget == null) {
            throw new NullPointerException("widget");
        }
        if (phase == null) {
            throw new NullPointerException("phase");
        }
        if (cause == null) {
            throw new NullPointerException("cause");
        }
    }
}