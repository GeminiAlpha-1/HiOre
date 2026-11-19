package dev.oreui.components;

import dev.oreui.components.platform.IRenderContext;
import dev.oreui.components.platform.IText;
import dev.oreui.components.platform.TextMetrics;
import dev.oreui.theme.ColorRole;
import dev.oreui.theme.TypeRole;

import java.util.List;

public final class OreContextMenu extends OreWidget {
    private static final float ITEM_HEIGHT = 20f;
    private static final float MIN_WIDTH = 80f;
    private static final float MAX_WIDTH = 200f;

    private final List<ContextMenuEntry> entries;
    private MenuPopup popup;
    private boolean open;

    public OreContextMenu(ComponentContext context, List<ContextMenuEntry> entries) {
        super(context);
        this.entries = List.copyOf(entries);
    }

    public boolean isOpen() {
        return open;
    }

    public void open(float x, float y) {
        OverlayManager manager = overlayManager();
        if (manager == null || entries.isEmpty()) {
            return;
        }
        if (popup == null) {
            popup = new MenuPopup(context, this, entries, measureWidth());
        }
        popup.layoutNode().frame().set(x, y, measureWidth(), entries.size() * ITEM_HEIGHT);
        popup.syncFrames();
        manager.push(popup);
        open = true;
    }

    public void close() {
        if (popup != null) {
            OverlayManager manager = overlayManager();
            if (manager != null) {
                manager.remove(popup);
            }
        }
        open = false;
    }

    @Override
    public void setOverlayManager(OverlayManager overlayManager) {
        super.setOverlayManager(overlayManager);
        if (open && overlayManager != null && popup != null) {
            overlayManager.push(popup);
        }
    }

    @Override
    public void syncFrames() {
        super.syncFrames();
        if (popup != null) {
            popup.syncFrames();
        }
    }

    private float measureWidth() {
        float width = MIN_WIDTH;
        for (ContextMenuEntry entry : entries) {
            TextMetrics metrics = context.textMeasurer().measure(entry.label(), TypeRole.LABEL, 0f);
            width = Math.max(width, metrics.width() + 22f);
        }
        return Math.min(MAX_WIDTH, width);
    }

    private static final class MenuPopup extends OreWidget {
        private final OreContextMenu owner;
        private final List<ContextMenuEntry> entries;
        private final float width;

        private MenuPopup(ComponentContext context, OreContextMenu owner,
                          List<ContextMenuEntry> entries, float width) {
            super(context);
            this.owner = owner;
            this.entries = entries;
            this.width = width;
        }

        @Override
        public boolean pointerDown(float x, float y) {
            int index = (int) ((y - bounds().y()) / ITEM_HEIGHT);
            if (index < 0 || index >= entries.size()) {
                owner.close();
                return true;
            }
            ContextMenuEntry entry = entries.get(index);
            owner.close();
            entry.action().run();
            return true;
        }

        @Override
        public void renderBackground(IRenderContext ctx) {
            fill(ctx, ColorRole.SURFACE_CARD, bounds().x(), bounds().y(), bounds().width(), bounds().height());
            outline(ctx, ColorRole.BORDER_OUTER, bounds().x(), bounds().y(),
                    bounds().width(), bounds().height(), 1f);
            fill(ctx, ColorRole.BEVEL_LIGHT, bounds().x() + 1f, bounds().y() + 1f,
                    bounds().width() - 2f, 1f);
            fill(ctx, ColorRole.BEVEL_DARK, bounds().x() + 1f, bounds().y() + bounds().height() - 2f,
                    bounds().width() - 2f, 1f);
            for (int i = 0; i < entries.size(); i++) {
                if (entries.get(i).danger()) {
                    fill(ctx, ColorRole.DANGER_DEFAULT, bounds().x() + 1f,
                            bounds().y() + i * ITEM_HEIGHT + 1f, width - 2f, ITEM_HEIGHT - 2f);
                } else if (i % 2 == 1) {
                    fill(ctx, ColorRole.SURFACE_BASE, bounds().x() + 1f,
                            bounds().y() + i * ITEM_HEIGHT + 1f, width - 2f, ITEM_HEIGHT - 2f);
                }
            }
        }

        @Override
        public void renderForeground(IRenderContext ctx) {
            for (int i = 0; i < entries.size(); i++) {
                ContextMenuEntry entry = entries.get(i);
                TextMetrics metrics = context.textMeasurer().measure(entry.label(), TypeRole.LABEL, 0f);
                text(ctx, entry.label(), bounds().x() + 10f,
                        textCenterY(bounds().y() + i * ITEM_HEIGHT, ITEM_HEIGHT, metrics),
                        TypeRole.LABEL, entry.danger() ? ColorRole.TEXT_ON_ACCENT : ColorRole.TEXT_PRIMARY);
            }
        }
    }
}