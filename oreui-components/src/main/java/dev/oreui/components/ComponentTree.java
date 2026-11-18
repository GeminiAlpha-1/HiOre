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

    public void layout(Rect2f viewport) {
        engine.compute(viewport);
        root.syncFrames();
        collectFocusables(root);
        if (!focusables.contains(focused)) {
            focused = focusables.isEmpty() ? null : focusables.get(0);
            if (focused != null) {
                focused.setFocused(true);
            }
        }
    }

    public void update(float deltaSeconds) {
        root.update(deltaSeconds);
    }

    public void renderBackground(IRenderContext ctx) {
        root.renderBackground(ctx);
        overlayManager.renderBackground(ctx);
    }

    public void renderForeground(IRenderContext ctx) {
        root.renderForeground(ctx);
        overlayManager.renderForeground(ctx);
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
                boolean capture = target.pointerDown(event.x(), event.y());
                if (capture) {
                    captured = target;
                }
                return true;
            }
            case POINTER_MOVE -> {
                if (captured != null) {
                    return captured.pointerMove(event.x(), event.y());
                }
                updateHover(root.hitTest(event.x(), event.y()));
                return false;
            }
            case POINTER_UP -> {
                OreWidget target = captured != null ? captured : root.hitTest(event.x(), event.y());
                captured = null;
                return target != null && target.pointerUp(event.x(), event.y());
            }
            case KEY_DOWN -> {
                if (event.code() == KeyCodes.TAB) {
                    return focusNext();
                }
                return focused != null && focused.keyPressed(event.code(), event.modifiers());
            }
            case CHAR_TYPED -> {
                return focused != null && focused.charTyped(event.character());
            }
            case WHEEL -> {
                OreWidget target = captured != null ? captured : root.hitTest(event.x(), event.y());
                return target != null && target.wheel(event.code());
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
        if (widget.isFocusable()) {
            focusables.add(widget);
        }
        for (OreWidget child : widget.children()) {
            collect(child);
        }
    }
}