package dev.oreui.components;

import dev.oreui.components.platform.IText;

public record ContextMenuEntry(IText label, boolean danger, Runnable action) {
    public ContextMenuEntry {
        action = action == null ? () -> {
        } : action;
    }
}