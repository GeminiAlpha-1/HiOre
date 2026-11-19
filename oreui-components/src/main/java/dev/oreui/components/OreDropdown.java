package dev.oreui.components;

import dev.oreui.components.platform.IRenderContext;
import dev.oreui.components.platform.IText;
import dev.oreui.components.platform.TextMetrics;
import dev.oreui.theme.ColorRole;
import dev.oreui.theme.TypeRole;

import java.util.List;

public final class OreDropdown extends OreInteractive {
    private static final int ICON_GRID = 8;
    private static final int[] CHEVRON_DOWN = {
            0b00000000, 0b00000000, 0b10000001, 0b01000010,
            0b00100100, 0b00011000, 0b00000000, 0b00000000,
    };
    private static final int[] CHEVRON_UP = {
            0b00000000, 0b00000000, 0b00011000, 0b00100100,
            0b01000010, 0b10000001, 0b00000000, 0b00000000,
    };

    private final List<IText> items;
    private final DropdownSize dropdownSize;
    private int selected = -1;
    private boolean open;
    private DropdownPopup popup;

    public OreDropdown(ComponentContext context, DropdownSize dropdownSize, List<IText> items) {
        super(context);
        this.dropdownSize = dropdownSize;
        this.items = List.copyOf(items);
        style().height(dropdownSize == DropdownSize.COMPACT ? 16f : 20f);
    }

    public int selected() {
        return selected;
    }

    public boolean isOpen() {
        return open;
    }

    public void select(int index) {
        if (index >= 0 && index < items.size()) {
            selected = index;
        }
    }

    public void setOpen(boolean open) {
        this.open = open;
        syncPopup();
    }

    @Override
    public void setOverlayManager(OverlayManager overlayManager) {
        super.setOverlayManager(overlayManager);
        syncPopup();
    }

    @Override
    public void syncFrames() {
        super.syncFrames();
        if (popup != null) {
            positionPopup();
            popup.syncFrames();
        }
    }

    private void syncPopup() {
        OverlayManager manager = overlayManager();
        if (manager == null) {
            return;
        }
        if (!open) {
            if (popup != null) {
                manager.remove(popup);
            }
            return;
        }
        if (popup == null) {
            popup = new DropdownPopup(context, this, items);
        }
        positionPopup();
        popup.syncFrames();
        manager.push(popup);
    }

    private void positionPopup() {
        popup.layoutNode().frame().set(bounds().x(), bounds().bottom(),
                bounds().width(), items.size() * 18f + 2f);
    }

    @Override
    public boolean pointerDown(float x, float y) {
        if (!isEnabled() || !contains(x, y)) {
            return false;
        }
        setOpen(!open);
        return true;
    }

    @Override
    public boolean pointerUp(float x, float y) {
        return open;
    }

    @Override
    public void renderBackground(IRenderContext ctx) {
        fill(ctx, ColorRole.SURFACE_CARD, bounds().x(), bounds().y(), bounds().width(), bounds().height());
        outline(ctx, ColorRole.BORDER_OUTER, bounds().x(), bounds().y(), bounds().width(), bounds().height(), 1f);
        fill(ctx, ColorRole.BEVEL_LIGHT, bounds().x() + 1f, bounds().y() + 1f, bounds().width() - 2f, 1f);
        fill(ctx, ColorRole.BEVEL_DARK, bounds().x() + 1f, bounds().y() + bounds().height() - 1f,
                bounds().width() - 2f, 1f);
        float arrowX = Math.round(bounds().x() + bounds().width() - 18f);
        float arrowY = Math.round(bounds().y() + (bounds().height() - ICON_GRID) * 0.5f);
        int[] arrow = open ? CHEVRON_UP : CHEVRON_DOWN;
        for (int row = 0; row < ICON_GRID; row++) {
            int bits = arrow[row];
            for (int col = 0; col < ICON_GRID; col++) {
                if ((bits & (1 << (ICON_GRID - 1 - col))) != 0) {
                    fill(ctx, ColorRole.ICON_PRIMARY, arrowX + col, arrowY + row, 1f, 1f);
                }
            }
        }
        drawFocus(ctx);
    }

    @Override
    public void renderForeground(IRenderContext ctx) {
        IText trigger = selected >= 0 ? items.get(selected) : new LiteralText("Select…");
        TextMetrics metrics = context.textMeasurer().measure(trigger, TypeRole.LABEL, 0f);
        text(ctx, trigger, bounds().x() + 5f,
                bounds().y() + textCenterY(1f, Math.max(1f, bounds().height() - 2f), metrics),
                TypeRole.LABEL, ColorRole.TEXT_PRIMARY);
    }

    private static final class DropdownPopup extends OreWidget {
        private final OreDropdown owner;
        private final List<IText> items;

        private DropdownPopup(ComponentContext context, OreDropdown owner, List<IText> items) {
            super(context);
            this.owner = owner;
            this.items = items;
        }

        @Override
        public boolean pointerDown(float x, float y) {
            int index = (int) ((y - bounds().y() - 1f) / 18f);
            owner.select(Math.max(0, Math.min(items.size() - 1, index)));
            owner.setOpen(false);
            return true;
        }

        @Override
        public void renderBackground(IRenderContext ctx) {
            float x = bounds().x();
            float y = bounds().y();
            float w = bounds().width();
            float h = bounds().height();
            fill(ctx, ColorRole.SURFACE_SUNKEN, x, y, w, h);
            for (int i = 0; i < items.size(); i++) {
                float itemY = y + 1f + i * 18f;
                fill(ctx, ColorRole.SURFACE_RAISED_DEFAULT, x + 1f, itemY, w - 2f, 17f);
                if (i > 0) {
                    fill(ctx, ColorRole.BEVEL_LIGHT, x + 1f, itemY - 1f, w - 2f, 1f);
                }
                if (i == owner.selected()) {
                    fill(ctx, ColorRole.ACCENT_DEFAULT, x + 1f, itemY, 2f, 17f);
                }
            }
            fill(ctx, ColorRole.BORDER_OUTER, x, y, w, 1f);
            fill(ctx, ColorRole.BORDER_OUTER, x, y + h - 1f, w, 1f);
            fill(ctx, ColorRole.BORDER_OUTER, x, y + 1f, 1f, h - 2f);
            fill(ctx, ColorRole.BORDER_OUTER, x + w - 1f, y + 1f, 1f, h - 2f);
            fill(ctx, ColorRole.BEVEL_LIGHT, x + 1f, y + 1f, w - 2f, 1f);
        }

        @Override
        public void renderForeground(IRenderContext ctx) {
            for (int i = 0; i < items.size(); i++) {
                float itemY = bounds().y() + 1f + i * 18f;
                TextMetrics metrics = context.textMeasurer().measure(items.get(i), TypeRole.LABEL, 0f);
                text(ctx, items.get(i), bounds().x() + 8f, textCenterY(itemY, 17f, metrics),
                        TypeRole.LABEL, i == owner.selected()
                                ? ColorRole.TEXT_PRIMARY : ColorRole.TEXT_SECONDARY);
                if (i == owner.selected()) {
                    float tickX = Math.round(bounds().right() - 18f);
                    float tickY = itemY + 5f;
                    fill(ctx, ColorRole.TEXT_PRIMARY, tickX, tickY + 2f, 2f, 2f);
                    fill(ctx, ColorRole.TEXT_PRIMARY, tickX + 2f, tickY + 4f, 2f, 2f);
                    fill(ctx, ColorRole.TEXT_PRIMARY, tickX + 4f, tickY + 2f, 2f, 2f);
                    fill(ctx, ColorRole.TEXT_PRIMARY, tickX + 6f, tickY, 2f, 2f);
                }
            }
        }
    }
}