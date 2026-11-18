package dev.oreui.layout;

import dev.oreui.core.geom.Insets;
import dev.oreui.core.geom.Rect2f;
import dev.vfyjxf.taffy.geometry.TaffyRect;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.AvailableSpace;
import dev.vfyjxf.taffy.style.FlexDirection;
import dev.vfyjxf.taffy.style.LengthPercentage;
import dev.vfyjxf.taffy.style.TaffyDimension;
import dev.vfyjxf.taffy.style.TaffyStyle;
import dev.vfyjxf.taffy.tree.Layout;
import dev.vfyjxf.taffy.tree.NodeId;
import dev.vfyjxf.taffy.tree.TaffyTree;

import java.util.IdentityHashMap;
import java.util.Map;

public final class LayoutEngine {
    private final TaffyTree tree = new TaffyTree();
    private final Map<LayoutNode, NodeId> ids = new IdentityHashMap<>();
    private final Map<LayoutNode, Long> stamps = new IdentityHashMap<>();
    private LayoutNode root;
    private NodeId rootId;
    private float viewportX = Float.NaN;
    private float viewportY = Float.NaN;
    private float viewportWidth = Float.NaN;
    private float viewportHeight = Float.NaN;
    private boolean autoSize;
    private int layoutCount;

    public void setRoot(LayoutNode root) {
        this.root = root;
        if (root != null) {
            root.markDirty();
        }
    }

    public LayoutNode root() {
        return root;
    }

    public void requestLayout() {
        if (root != null) {
            root.markDirty();
        }
    }

    public int layoutCount() {
        return layoutCount;
    }

    public void compute(Rect2f viewport) {
        if (root == null) {
            return;
        }
        float width = viewport.width();
        float height = viewport.height();
        boolean viewportChanged = width != viewportWidth || height != viewportHeight
                || viewport.x() != viewportX || viewport.y() != viewportY;
        if (!root.isDirty() && !viewportChanged) {
            return;
        }

        if (viewportChanged || structureChanged(root)) {
            rebuild(width, height);
        }
        refreshStyles(root, true, width, height);
        if (autoSize) {
            tree.computeLayout(rootId, new TaffySize<>(AvailableSpace.maxContent(), AvailableSpace.maxContent()));
        }
        tree.computeLayout(rootId, new TaffySize<>(AvailableSpace.definite(width), AvailableSpace.definite(height)));
        apply(root, viewport.x(), viewport.y());
        viewportX = viewport.x();
        viewportY = viewport.y();
        viewportWidth = width;
        viewportHeight = height;
        layoutCount++;
    }

    private void rebuild(float width, float height) {
        tree.clear();
        ids.clear();
        stamps.clear();
        rootId = build(root, true, width, height);
        autoSize = false;
        detectAutoSize(root, true);
    }

    private void detectAutoSize(LayoutNode node, boolean rootNode) {
        if (!rootNode && (Float.isNaN(node.style().width()) || Float.isNaN(node.style().height()))) {
            autoSize = true;
        }
        for (LayoutNode child : node.children()) {
            detectAutoSize(child, false);
        }
    }

    private boolean structureChanged(LayoutNode node) {
        Long stamp = stamps.get(node);
        if (stamp == null || stamp.longValue() != node.structureStamp()) {
            return true;
        }
        for (LayoutNode child : node.children()) {
            if (structureChanged(child)) {
                return true;
            }
        }
        return false;
    }

    private void refreshStyles(LayoutNode node, boolean rootNode, float width, float height) {
        if (node.style().consumeDirty()) {
            tree.setStyle(ids.get(node), toTaffy(node.style(), rootNode, width, height));
            if (!rootNode && (Float.isNaN(node.style().width()) || Float.isNaN(node.style().height()))) {
                autoSize = true;
            }
        }
        for (LayoutNode child : node.children()) {
            refreshStyles(child, false, width, height);
        }
    }

    private NodeId build(LayoutNode node, boolean rootNode, float width, float height) {
        NodeId id = tree.newLeaf(toTaffy(node.style(), rootNode, width, height));
        ids.put(node, id);
        stamps.put(node, node.structureStamp());
        for (LayoutNode child : node.children()) {
            tree.addChild(id, build(child, false, width, height));
        }
        return id;
    }

    private TaffyStyle toTaffy(LayoutStyle source, boolean rootNode, float width, float height) {
        TaffyStyle style = new TaffyStyle();
        style.flexDirection = source.direction() == LayoutStyle.Direction.COLUMN
                ? FlexDirection.COLUMN : FlexDirection.ROW;
        style.size = rootNode
                ? TaffySize.of(TaffyDimension.length(width), TaffyDimension.length(height))
                : TaffySize.of(dimension(source.width()), dimension(source.height()));
        style.minSize = TaffySize.of(dimension(source.minWidth()), dimension(source.minHeight()));
        Insets padding = source.padding();
        style.padding = TaffyRect.of(
                LengthPercentage.length(padding.left()),
                LengthPercentage.length(padding.right()),
                LengthPercentage.length(padding.top()),
                LengthPercentage.length(padding.bottom()));
        style.gap = TaffySize.of(
                LengthPercentage.length(source.columnGap()),
                LengthPercentage.length(source.rowGap()));
        style.flexGrow = source.flexGrow();
        style.alignItems = alignItems(source.alignItems());
        style.justifyContent = justifyContent(source.justifyContent());
        return style;
    }

    private static TaffyDimension dimension(float value) {
        return Float.isNaN(value) ? TaffyDimension.auto() : TaffyDimension.length(value);
    }

    private static AlignItems alignItems(LayoutStyle.Align align) {
        return switch (align) {
            case START -> AlignItems.FLEX_START;
            case CENTER -> AlignItems.CENTER;
            case END -> AlignItems.FLEX_END;
            case STRETCH -> AlignItems.STRETCH;
        };
    }

    private static AlignContent justifyContent(LayoutStyle.Justify justify) {
        return switch (justify) {
            case START -> AlignContent.FLEX_START;
            case CENTER -> AlignContent.CENTER;
            case END -> AlignContent.FLEX_END;
            case SPACE_BETWEEN -> AlignContent.SPACE_BETWEEN;
        };
    }

    private void apply(LayoutNode node, float parentX, float parentY) {
        NodeId id = ids.get(node);
        Layout layout = tree.getLayout(id);
        float x = parentX + layout.location().x;
        float y = parentY + layout.location().y;
        node.frame().set(x, y, layout.size().width, layout.size().height);
        for (LayoutNode child : node.children()) {
            apply(child, x, y);
        }
        node.clearDirty();
    }
}