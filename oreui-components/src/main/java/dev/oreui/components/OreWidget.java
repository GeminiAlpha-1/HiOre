package dev.oreui.components;

import dev.oreui.components.platform.IRenderContext;
import dev.oreui.components.platform.IText;
import dev.oreui.core.geom.Rect2f;
import dev.oreui.layout.LayoutNode;
import dev.oreui.layout.LayoutStyle;
import dev.oreui.theme.ColorRole;
import dev.oreui.theme.TypeRole;

import java.util.ArrayList;
import java.util.List;

public abstract class OreWidget {
    protected final ComponentContext context;
    private final LayoutNode layout = new LayoutNode();
    private final Rect2f bounds = new Rect2f();
    private final Rect2f scratch = new Rect2f();
    private final List<OreWidget> children = new ArrayList<>();
    private OreWidget parent;
    private OverlayManager overlayManager;

    protected OreWidget(ComponentContext context) {
        this.context = context;
    }

    public LayoutNode layoutNode() {
        return layout;
    }

    public LayoutStyle style() {
        return layout.style();
    }

    public Rect2f bounds() {
        return bounds;
    }

    public List<OreWidget> children() {
        return children;
    }

    public OreWidget parent() {
        return parent;
    }

    public OreWidget addChild(OreWidget child) {
        if (child == this) {
            throw new IllegalArgumentException("widget cannot be its own child");
        }
        if (child.parent == this) {
            return this;
        }
        if (child.parent != null) {
            child.parent.removeChild(child);
        }
        child.parent = this;
        children.add(child);
        layout.addChild(child.layout);
        return this;
    }

    public boolean removeChild(OreWidget child) {
        if (child.parent != this) {
            return false;
        }
        children.remove(child);
        child.parent = null;
        layout.removeChild(child.layout);
        return true;
    }

    public void syncFrames() {
        bounds.set(layout.frame());
        for (OreWidget child : children) {
            child.syncFrames();
        }
    }

    public void update(float deltaSeconds) {
        for (OreWidget child : children) {
            child.update(deltaSeconds);
        }
    }

    public void renderBackground(IRenderContext ctx) {
        for (OreWidget child : children) {
            child.renderBackground(ctx);
        }
    }

    public void renderForeground(IRenderContext ctx) {
        for (OreWidget child : children) {
            child.renderForeground(ctx);
        }
    }

    public OreWidget hitTest(float x, float y) {
        for (int i = children.size() - 1; i >= 0; i--) {
            OreWidget child = children.get(i);
            if (child.contains(x, y)) {
                OreWidget hit = child.hitTest(x, y);
                if (hit != null) {
                    return hit;
                }
            }
        }
        return contains(x, y) ? this : null;
    }

    public boolean contains(float x, float y) {
        return !bounds.isEmpty() && x >= bounds.x() && x < bounds.right()
                && y >= bounds.y() && y < bounds.bottom();
    }

    public boolean isFocusable() {
        return false;
    }

    public void setFocused(boolean focused) {
    }

    public void setHovered(boolean hovered) {
    }

    public boolean pointerDown(float x, float y) {
        return false;
    }

    public boolean pointerMove(float x, float y) {
        return false;
    }

    public boolean pointerUp(float x, float y) {
        return false;
    }

    public boolean keyPressed(int key, int modifiers) {
        return false;
    }

    public boolean charTyped(char character) {
        return false;
    }

    public boolean wheel(int delta) {
        return false;
    }

    public void markDirty() {
        layout.markDirty();
    }

    public void setOverlayManager(OverlayManager overlayManager) {
        this.overlayManager = overlayManager;
        for (OreWidget child : children) {
            child.setOverlayManager(overlayManager);
        }
    }

    public OverlayManager overlayManager() {
        return overlayManager;
    }

    protected int color(ColorRole role) {
        return context.theme().color(role);
    }

    protected void fill(IRenderContext ctx, ColorRole role, float x, float y, float width, float height) {
        if (width <= 0f || height <= 0f) {
            return;
        }
        scratch.set(x, y, width, height);
        ctx.submitPixelRect(scratch, color(role));
    }

    protected void outline(IRenderContext ctx, ColorRole role, float x, float y,
                           float width, float height, float thickness) {
        fill(ctx, role, x, y, width, thickness);
        fill(ctx, role, x, y + height - thickness, width, thickness);
        fill(ctx, role, x, y + thickness, thickness, height - thickness * 2f);
        fill(ctx, role, x + width - thickness, y + thickness, thickness, height - thickness * 2f);
    }

    protected void text(IRenderContext ctx, IText text, float x, float y, TypeRole role, ColorRole color) {
        ctx.submitText(text, x, y, role, color);
    }
}