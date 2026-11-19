package dev.oreui.components;

import dev.oreui.components.platform.IRenderContext;
import dev.oreui.components.platform.IText;
import dev.oreui.components.platform.TextMetrics;
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
    private WidgetFailure failure;

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

    public boolean isQuarantined() {
        return failure != null;
    }

    public WidgetFailure failure() {
        return failure;
    }

    void quarantine(String phase, Throwable cause) {
        if (failure == null) {
            failure = new WidgetFailure(this, phase, cause);
        }
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
        if (isQuarantined()) {
            return;
        }
        bounds.set(layout.frame());
        for (OreWidget child : children) {
            if (child.isQuarantined()) {
                continue;
            }
            try {
                child.syncFrames();
            } catch (Throwable throwable) {
                child.quarantine("layout", throwable);
            }
        }
    }

    public void update(float deltaSeconds) {
        if (isQuarantined()) {
            return;
        }
        for (OreWidget child : children) {
            if (child.isQuarantined()) {
                continue;
            }
            try {
                child.update(deltaSeconds);
            } catch (Throwable throwable) {
                child.quarantine("update", throwable);
            }
        }
    }

    public void renderBackground(IRenderContext ctx) {
        if (isQuarantined()) {
            return;
        }
        for (OreWidget child : children) {
            if (child.isQuarantined()) {
                continue;
            }
            try {
                child.renderBackground(ctx);
            } catch (Throwable throwable) {
                child.quarantine("renderBackground", throwable);
            }
        }
    }

    public void renderForeground(IRenderContext ctx) {
        if (isQuarantined()) {
            return;
        }
        for (OreWidget child : children) {
            if (child.isQuarantined()) {
                continue;
            }
            try {
                child.renderForeground(ctx);
            } catch (Throwable throwable) {
                child.quarantine("renderForeground", throwable);
            }
        }
    }

    public OreWidget hitTest(float x, float y) {
        if (isQuarantined()) {
            return null;
        }
        for (int i = children.size() - 1; i >= 0; i--) {
            OreWidget child = children.get(i);
            if (child.isQuarantined()) {
                continue;
            }
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
        return !isQuarantined() && !bounds.isEmpty() && x >= bounds.x() && x < bounds.right()
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

    public boolean charTyped(int codepoint) {
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

    protected void fillRounded(IRenderContext ctx, ColorRole role, float x, float y,
                               float width, float height, float radius) {
        if (width <= 0f || height <= 0f) {
            return;
        }
        scratch.set(x, y, width, height);
        ctx.submitSdfRect(scratch, color(role), radius, 0f);
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

    protected void textClipped(IRenderContext ctx, IText text, float x, float y, TypeRole role,
                               ColorRole color, Rect2f clip) {
        ctx.submitTextClipped(text, x, y, role, color, clip);
    }

    /**
     * Returns the renderer y (line top) that places the measured ink box at the vertical center
     * of {@code [top, top + height]}.
     */
    protected float textCenterY(float top, float height, TextMetrics metrics) {
        int inkHeight = metrics.inkHeight();
        if (inkHeight <= 0) {
            return Math.round(top + (height - metrics.glyphHeight()) * 0.5f);
        }
        float inkTop = top + (height - inkHeight) * 0.5f;
        return Math.round(inkTop) - metrics.inkTop();
    }
}