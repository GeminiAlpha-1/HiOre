package dev.oreui.layout;

import dev.oreui.core.geom.Insets;

public final class LayoutStyle {
    public enum Direction {
        ROW,
        COLUMN
    }

    public enum Align {
        START,
        CENTER,
        END,
        STRETCH
    }

    public enum Justify {
        START,
        CENTER,
        END,
        SPACE_BETWEEN
    }

    private Direction direction = Direction.ROW;
    private Align alignItems = Align.STRETCH;
    private Justify justifyContent = Justify.START;
    private float width = Float.NaN;
    private float height = Float.NaN;
    private float minWidth;
    private float minHeight;
    private float rowGap;
    private float columnGap;
    private float flexGrow;
    private final Insets padding = new Insets();
    private boolean dirty = true;

    public Direction direction() {
        return direction;
    }

    public LayoutStyle direction(Direction value) {
        direction = value;
        dirty = true;
        return this;
    }

    public Align alignItems() {
        return alignItems;
    }

    public LayoutStyle alignItems(Align value) {
        alignItems = value;
        dirty = true;
        return this;
    }

    public Justify justifyContent() {
        return justifyContent;
    }

    public LayoutStyle justifyContent(Justify value) {
        justifyContent = value;
        dirty = true;
        return this;
    }

    public float width() {
        return width;
    }

    public LayoutStyle width(float value) {
        width = value;
        dirty = true;
        return this;
    }

    public float height() {
        return height;
    }

    public LayoutStyle height(float value) {
        height = value;
        dirty = true;
        return this;
    }

    public float minWidth() {
        return minWidth;
    }

    public LayoutStyle minWidth(float value) {
        minWidth = value;
        dirty = true;
        return this;
    }

    public float minHeight() {
        return minHeight;
    }

    public LayoutStyle minHeight(float value) {
        minHeight = value;
        dirty = true;
        return this;
    }

    public float rowGap() {
        return rowGap;
    }

    public float columnGap() {
        return columnGap;
    }

    public LayoutStyle gap(float value) {
        rowGap = value;
        columnGap = value;
        dirty = true;
        return this;
    }

    public LayoutStyle rowGap(float value) {
        rowGap = value;
        dirty = true;
        return this;
    }

    public LayoutStyle columnGap(float value) {
        columnGap = value;
        dirty = true;
        return this;
    }

    public float flexGrow() {
        return flexGrow;
    }

    public LayoutStyle flexGrow(float value) {
        flexGrow = value;
        dirty = true;
        return this;
    }

    public Insets padding() {
        return padding;
    }

    public LayoutStyle padding(Insets value) {
        padding.set(value);
        dirty = true;
        return this;
    }

    public LayoutStyle copyFrom(LayoutStyle other) {
        direction = other.direction;
        alignItems = other.alignItems;
        justifyContent = other.justifyContent;
        width = other.width;
        height = other.height;
        minWidth = other.minWidth;
        minHeight = other.minHeight;
        rowGap = other.rowGap;
        columnGap = other.columnGap;
        flexGrow = other.flexGrow;
        padding.set(other.padding);
        dirty = true;
        return this;
    }

    public boolean consumeDirty() {
        boolean changed = dirty;
        dirty = false;
        return changed;
    }
}