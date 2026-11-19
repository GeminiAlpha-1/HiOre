package dev.oreui.components;

import dev.oreui.components.platform.IInputEvent;
import dev.oreui.components.platform.IRenderContext;
import dev.oreui.core.geom.Rect2f;
import dev.oreui.layout.LayoutEngine;

import java.util.ArrayList;
import java.util.List;

public final class ComponentTree {
    private final OreWidget root;
    private final LayoutEngine engine = new LayoutEngine();
    private final OverlayManager overlayManager = new OverlayManager();
    private final List<OreWidget> focusables = new ArrayList<>();
    private OreWidget focused;
    private OreWidget captured;
    private OreWidget hovered;

    public ComponentTree(OreWidget root) {
        if (root == null) {
            throw new NullPointerException("root");
        }
        this.root = root;
        root.setOverlayManager(overlayManager);
        engine.setRoot(root.layoutNode());
    }

    public OverlayManager overlayManager() {
        return overlayManager;
    }

    public OreWidget root() {
        return root;
    }

    public OreWidget focused() {
        return focused;
    }

    public List<WidgetFailure> failures() {
        List<WidgetFailure> failures = new ArrayList<>();
        collectFailures(root, failures);
        return List.copyOf(failures);
    }

    public void layout(Rect2f viewport) {
        if (root.isQuarantined()) {
            return;
        }
        try {
            engine.compute(viewport);
            root.syncFrames();
        } catch (Throwable throwable) {
            root.quarantine("layout", throwable);
            return;
        }
        collectFocusables(root);
        if (focused != null && focused.isQuarantined()) {
            focused = null;
        }
        if (!focusables.contains(focused)) {
            focused = focusables.isEmpty() ? null : focusables.get(0);
            if (focused != null) {
                focused.setFocused(true);
            }
        }
    }

    public void update(float deltaSeconds) {
        if (root.isQuarantined()) {
            return;
        }
        try {
            root.update(deltaSeconds);
        } catch (Throwable throwable) {
            root.quarantine("update", throwable);
        }
    }

    public void renderBackground(IRenderContext ctx) {
        if (!root.isQuarantined()) {
            try {
                root.renderBackground(ctx);
            } catch (Throwable throwable) {
                root.quarantine("renderBackground", throwable);
            }
        }
        try {
            overlayManager.renderBackground(ctx);
        } catch (Throwable throwable) {
            root.quarantine("overlayBackground", throwable);
        }
    }

    public void renderForeground(IRenderContext ctx) {
        if (!root.isQuarantined()) {
            try {
                root.renderForeground(ctx);
            } catch (Throwable throwable) {
                root.quarantine("renderForeground", throwable);
            }
        }
        try {
            overlayManager.renderForeground(ctx);
        } catch (Throwable throwable) {
            root.quarantine("overlayForeground", throwable);
        }
    }

    public boolean dispatch(IInputEvent event) {
        if (event == null) {
            return false;
        }
        if (overlayManager.dispatch(event)) {
            return true;
        }
        switch (event.kind()) {
            case POINTER_DOWN -> {
                OreWidget target = root.hitTest(event.x(), event.y());
                if (target == null) {
                    return false;
                }
                updateHover(target);
                if (target.isFocusable()) {
                    setFocused(target);
                }
                boolean capture;
                try {
                    capture = target.pointerDown(event.x(), event.y());
                } catch (Throwable throwable) {
                    target.quarantine("pointerDown", throwable);
                    return false;
                }
                if (capture) {
                    captured = target;
                }
                return true;
            }
            case POINTER_MOVE -> {
                if (captured != null) {
                    try {
                        return captured.pointerMove(event.x(), event.y());
                    } catch (Throwable throwable) {
                        captured.quarantine("pointerMove", throwable);
                        captured = null;
                        return false;
                    }
                }
                updateHover(root.hitTest(event.x(), event.y()));
                return false;
            }
            case POINTER_UP -> {
                OreWidget target = captured != null ? captured : root.hitTest(event.x(), event.y());
                captured = null;
                if (target == null) {
                    return false;
                }
                try {
                    return target.pointerUp(event.x(), event.y());
                } catch (Throwable throwable) {
                    target.quarantine("pointerUp", throwable);
                    return false;
                }
            }
            case KEY_DOWN -> {
                if (event.code() == KeyCodes.TAB) {
                    return focusNext();
                }
                if (focused == null) {
                    return false;
                }
                try {
                    return focused.keyPressed(event.code(), event.modifiers());
                } catch (Throwable throwable) {
                    focused.quarantine("keyPressed", throwable);
                    return false;
                }
            }
            case CHAR_TYPED -> {
                if (focused == null) {
                    return false;
                }
                try {
                    return focused.charTyped(event.character());
                } catch (Throwable throwable) {
                    focused.quarantine("charTyped", throwable);
                    return false;
                }
            }
            case WHEEL -> {
                OreWidget target = captured != null ? captured : root.hitTest(event.x(), event.y());
                for (OreWidget current = target; current != null; current = current.parent()) {
                    if (current.isQuarantined()) {
                        continue;
                    }
                    try {
                        if (current.wheel(event.code())) {
                            return true;
                        }
                    } catch (Throwable throwable) {
                        current.quarantine("wheel", throwable);
                    }
                }
                return false;
            }
            default -> {
                return false;
            }
        }
    }

    private void updateHover(OreWidget target) {
        if (hovered == target) {
            return;
        }
        if (hovered != null) {
            hovered.setHovered(false);
        }
        hovered = target;
        if (hovered != null) {
            hovered.setHovered(true);
        }
    }

    private boolean focusNext() {
        if (focusables.isEmpty()) {
            return false;
        }
        int index = focused == null ? -1 : focusables.indexOf(focused);
        int next = (index + 1) % focusables.size();
        setFocused(focusables.get(next));
        return true;
    }

    private void setFocused(OreWidget target) {
        if (target != null && target.isQuarantined()) {
            return;
        }
        if (focused == target) {
            return;
        }
        if (focused != null) {
            focused.setFocused(false);
        }
        focused = target;
        if (focused != null) {
            focused.setFocused(true);
        }
    }

    private void collectFocusables(OreWidget widget) {
        focusables.clear();
        collect(widget);
    }

    private void collect(OreWidget widget) {
        if (widget.isQuarantined()) {
            return;
        }
        if (widget.isFocusable()) {
            focusables.add(widget);
        }
        for (OreWidget child : widget.children()) {
            collect(child);
        }
    }

    private void collectFailures(OreWidget widget, List<WidgetFailure> failures) {
        if (widget.failure() != null) {
            failures.add(widget.failure());
        }
        for (OreWidget child : widget.children()) {
            collectFailures(child, failures);
        }
    }
}