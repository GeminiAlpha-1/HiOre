package dev.oreui.components;

import dev.oreui.components.platform.IRenderContext;
import dev.oreui.components.platform.IText;
import dev.oreui.components.platform.TextMetrics;
import dev.oreui.theme.ColorRole;
import dev.oreui.theme.TypeRole;

import java.util.List;

public final class OreDropdown extends OreInteractive {
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
        popup.layoutNode().frame().set(bounds().x(), bounds().bottom(), bounds().width(), items.size() * 18f);
        popup.syncFrames();
        manager.push(popup);
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
        fill(ctx, ColorRole.ICON_PRIMARY, bounds().x() + bounds().width() - 12f,
                bounds().y() + 7f, 2f, 2f);
        fill(ctx, ColorRole.ICON_PRIMARY, bounds().x() + bounds().width() - 10f,
                bounds().y() + 9f, 2f, 2f);
        fill(ctx, ColorRole.ICON_PRIMARY, bounds().x() + bounds().width() - 14f,
                bounds().y() + 9f, 2f, 2f);
        drawFocus(ctx);
    }

    @Override
    public void renderForeground(IRenderContext ctx) {
        IText trigger = selected >= 0 ? items.get(selected) : new LiteralText("Select…");
        TextMetrics metrics = context.textMeasurer().measure(trigger, TypeRole.LABEL, 0f);
        text(ctx, trigger, bounds().x() + 5f, bounds().y() + textCenterY(0f, bounds().height(), metrics),
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
            int index = (int) ((y - bounds().y()) / 18f);
            owner.select(Math.max(0, Math.min(items.size() - 1, index)));
            owner.setOpen(false);
            return true;
        }

        @Override
        public void renderBackground(IRenderContext ctx) {
            fill(ctx, ColorRole.SURFACE_CARD, bounds().x(), bounds().y(), bounds().width(), bounds().height());
            outline(ctx, ColorRole.BORDER_OUTER, bounds().x(), bounds().y(), bounds().width(), bounds().height(), 1f);
            if (owner.selected() >= 0) {
                fill(ctx, ColorRole.ACCENT_DEFAULT, bounds().x() + 1f, bounds().y() + owner.selected() * 18f + 1f,
                        2f, 16f);
            }
        }

        @Override
        public void renderForeground(IRenderContext ctx) {
            for (int i = 0; i < items.size(); i++) {
                text(ctx, items.get(i), bounds().x() + 8f, bounds().y() + i * 18f + 4f,
                        TypeRole.LABEL, i == owner.selected() ? ColorRole.TEXT_PRIMARY : ColorRole.TEXT_SECONDARY);
            }
        }
    }
}