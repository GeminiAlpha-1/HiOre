package dev.oreui.layout;

import dev.oreui.core.geom.Rect2f;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class LayoutNode {
    private static final AtomicLong STRUCTURE_CLOCK = new AtomicLong();

    private final LayoutStyle style = new LayoutStyle();
    private final List<LayoutNode> children = new ArrayList<>();
    private final Rect2f frame = new Rect2f();
    private LayoutNode parent;
    private boolean dirty = true;
    private long structureStamp;

    public LayoutNode() {
    }

    public LayoutNode(LayoutStyle style) {
        this.style.copyFrom(style);
    }

    public LayoutStyle style() {
        return style;
    }

    public LayoutNode setStyle(LayoutStyle value) {
        style.copyFrom(value);
        markDirty();
        return this;
    }

    public LayoutNode addChild(LayoutNode child) {
        if (child == this) {
            throw new IllegalArgumentException("node cannot be its own child");
        }
        if (child.parent == this) {
            return this;
        }
        if (child.parent != null) {
            child.parent.removeChild(child);
        }
        child.parent = this;
        children.add(child);
        markStructure();
        markDirty();
        return this;
    }

    public boolean removeChild(LayoutNode child) {
        if (child.parent != this) {
            return false;
        }
        children.remove(child);
        child.parent = null;
        child.markDirty();
        markStructure();
        markDirty();
        return true;
    }

    public List<LayoutNode> children() {
        return children;
    }

    public LayoutNode parent() {
        return parent;
    }

    public Rect2f frame() {
        return frame;
    }

    public void markDirty() {
        LayoutNode node = this;
        while (node != null && !node.dirty) {
            node.dirty = true;
            node = node.parent;
        }
    }

    public long structureStamp() {
        return structureStamp;
    }

    private void markStructure() {
        structureStamp = STRUCTURE_CLOCK.incrementAndGet();
    }

    public boolean isDirty() {
        return dirty;
    }

    void clearDirty() {
        dirty = false;
    }
}