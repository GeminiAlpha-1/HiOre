package dev.oreui.components;

import dev.oreui.components.platform.IGameObjectRenderer.IItemHandle;
import dev.oreui.layout.LayoutStyle;
import dev.oreui.theme.Space;

import java.util.ArrayList;
import java.util.List;

public final class OreItemGrid extends OreWidget {
    private static final int DEFAULT_COLUMNS = 9;

    private final int columns;
    private final SlotStyle slotStyle;
    private final List<OreWidget> rows = new ArrayList<>();
    private final List<OreItemSlot> slots = new ArrayList<>();

    public OreItemGrid(ComponentContext context) {
        this(context, DEFAULT_COLUMNS);
    }

    public OreItemGrid(ComponentContext context, int columns) {
        this(context, columns, SlotStyle.STANDARD_18);
    }

    public OreItemGrid(ComponentContext context, int columns, SlotStyle slotStyle) {
        super(context);
        if (columns < 1) {
            throw new IllegalArgumentException("columns must be positive");
        }
        this.columns = columns;
        this.slotStyle = slotStyle;
        style().direction(LayoutStyle.Direction.COLUMN);
        style().gap(Space.NONE.value());
        style().width(columns * slotStyle.outer());
        style().height(0f);
    }

    public int columns() {
        return columns;
    }

    public List<OreItemSlot> slots() {
        return slots;
    }

    public void setItems(List<IItemHandle> items) {
        for (OreWidget row : rows) {
            removeChild(row);
        }
        rows.clear();
        slots.clear();
        int size = items == null ? 0 : items.size();
        int index = 0;
        while (index < size) {
            OreWidget row = new ItemRow(context);
            row.style().height(slotStyle.outer());
            for (int column = 0; column < columns && index < size; column++, index++) {
                OreItemSlot slot = new OreItemSlot(context, slotStyle);
                slot.setItem(items.get(index));
                row.addChild(slot);
                slots.add(slot);
            }
            addChild(row);
            rows.add(row);
        }
        style().height(rows.size() * slotStyle.outer());
        markDirty();
    }

    private static final class ItemRow extends OreWidget {
        private ItemRow(ComponentContext context) {
            super(context);
            style().direction(LayoutStyle.Direction.ROW);
            style().gap(Space.NONE.value());
        }
    }
}