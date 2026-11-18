package dev.oreui.components;

import dev.oreui.components.platform.IInputEvent;
import dev.oreui.components.platform.IRenderContext;

import java.util.ArrayList;
import java.util.List;

public final class OverlayManager {
    private final List<OreWidget> overlays = new ArrayList<>();

    public void push(OreWidget overlay) {
        if (overlay != null && !overlays.contains(overlay)) {
            overlays.add(overlay);
        }
    }

    public void remove(OreWidget overlay) {
        overlays.remove(overlay);
    }

    public OreWidget top() {
        return overlays.isEmpty() ? null : overlays.get(overlays.size() - 1);
    }

    public List<OreWidget> overlays() {
        return overlays;
    }

    public void renderBackground(IRenderContext ctx) {
        for (OreWidget overlay : overlays) {
            overlay.renderBackground(ctx);
        }
    }

    public void renderForeground(IRenderContext ctx) {
        for (OreWidget overlay : overlays) {
            overlay.renderForeground(ctx);
        }
    }

    public boolean dispatch(IInputEvent event) {
        for (int i = overlays.size() - 1; i >= 0; i--) {
            OreWidget overlay = overlays.get(i);
            if (overlay.contains(event.x(), event.y())) {
                return switch (event.kind()) {
                    case POINTER_DOWN -> overlay.pointerDown(event.x(), event.y());
                    case POINTER_UP -> overlay.pointerUp(event.x(), event.y());
                    case POINTER_MOVE -> overlay.pointerMove(event.x(), event.y());
                    case WHEEL -> overlay.wheel(event.code());
                    default -> false;
                };
            }
        }
        return false;
    }
}