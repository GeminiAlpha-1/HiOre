package dev.oreui.components;

import dev.oreui.components.platform.IRenderContext;
import dev.oreui.theme.ColorRole;

import dev.oreui.theme.SizeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class OreVirtualList<T> extends OreWidget {
    private static final float BORDER = 1f;
    private static final float SCROLLBAR_PAD = 2f;

    private final RowHeightPolicy policy;
    private final float rowHeight;
    private final List<T> items;
    private final VirtualRowFactory<T> factory;
    private final Map<Integer, OreWidget> cache = new HashMap<>();
    private final List<OreWidget> rows = new ArrayList<>();
    private float scroll;
    private int firstIndex;

    public OreVirtualList(ComponentContext context, RowHeightPolicy policy, float rowHeight,
                          List<T> items, VirtualRowFactory<T> factory) {
        super(context);
        if (rowHeight <= 0f) {
            throw new IllegalArgumentException("rowHeight must be positive");
        }
        this.policy = policy;
        this.rowHeight = rowHeight;
        this.items = new ArrayList<>(items);
        this.factory = factory;
    }

    public RowHeightPolicy policy() {
        return policy;
    }

    public float rowHeight() {
        return rowHeight;
    }

    public float scroll() {
        return scroll;
    }

    public int instantiatedRows() {
        return rows.size();
    }

    public int firstVisibleIndex() {
        return firstIndex;
    }

    public void setItems(List<T> next) {
        items.clear();
        items.addAll(next);
        cache.clear();
        rows.clear();
        scroll = clampScroll(scroll);
        rebuild();
        markDirty();
    }

    public void setScroll(float offset) {
        scroll = clampScroll(offset);
        rebuild();
        if (!bounds().isEmpty()) {
            syncFrames();
        }
    }

    @Override
    public void syncFrames() {
        super.syncFrames();
        rebuild();
        for (int i = 0; i < rows.size(); i++) {
            OreWidget row = rows.get(i);
            row.layoutNode().frame().set(bounds().x() + BORDER,
                    bounds().y() + (firstIndex + i) * rowHeight - scroll,
                    bounds().width() - BORDER * 2f, rowHeight);
            row.syncFrames();
        }
    }

    @Override
    public void update(float deltaSeconds) {
        super.update(deltaSeconds);
        for (OreWidget row : rows) {
            row.update(deltaSeconds);
        }
    }

    @Override
    public void setOverlayManager(OverlayManager overlayManager) {
        super.setOverlayManager(overlayManager);
        for (OreWidget row : rows) {
            row.setOverlayManager(overlayManager);
        }
    }

    @Override
    public boolean wheel(int delta) {
        setScroll(scroll - delta * rowHeight);
        return true;
    }

    @Override
    public boolean pointerDown(float x, float y) {
        OreWidget row = rowAt(y);
        return row != null && row.pointerDown(x, y);
    }

    @Override
    public boolean pointerUp(float x, float y) {
        OreWidget row = rowAt(y);
        return row != null && row.pointerUp(x, y);
    }

    @Override
    public boolean pointerMove(float x, float y) {
        OreWidget row = rowAt(y);
        return row != null && row.pointerMove(x, y);
    }

    @Override
    public void renderBackground(IRenderContext ctx) {
        fill(ctx, ColorRole.SURFACE_SUNKEN, bounds().x(), bounds().y(), bounds().width(), bounds().height());
        outline(ctx, ColorRole.BORDER_OUTER, bounds().x(), bounds().y(),
                bounds().width(), bounds().height(), BORDER);
        ctx.pushClip(bounds());
        try {
            for (OreWidget row : rows) {
                row.renderBackground(ctx);
            }
        } finally {
            ctx.popClip();
        }
        drawScrollbar(ctx);
    }

    @Override
    public void renderForeground(IRenderContext ctx) {
        ctx.pushClip(bounds());
        try {
            for (OreWidget row : rows) {
                row.renderForeground(ctx);
            }
        } finally {
            ctx.popClip();
        }
    }

    private void drawScrollbar(IRenderContext ctx) {
        float trackWidth = SizeToken.SCROLLBAR_W.value();
        float x = bounds().right() - trackWidth - SCROLLBAR_PAD;
        float y = bounds().y() + BORDER;
        float height = bounds().height() - BORDER * 2f;
        fill(ctx, ColorRole.SCROLL_TRACK, x, y, trackWidth, height);
        float content = items.size() * rowHeight;
        if (content <= 0f) {
            return;
        }
        float thumbHeight = Math.max(SizeToken.SCROLLBAR_MIN_THUMB.value(),
                height * (height / Math.max(height, content)));
        float maxScroll = Math.max(1f, content - height);
        float offset = ((height - thumbHeight) * (scroll / maxScroll));
        fill(ctx, ColorRole.SCROLL_THUMB, x, y + offset, trackWidth, thumbHeight);
    }

    private void rebuild() {
        if (items.isEmpty()) {
            rows.clear();
            cache.clear();
            firstIndex = 0;
            return;
        }
float height = bounds().height() > 0f ? bounds().height() : rowHeight;
        int visible = Math.max(1, (int) Math.ceil(height / rowHeight));
        int first = Math.max(0, (int) (scroll / rowHeight) - 1);
        int last = Math.min(items.size() - 1, first + visible + 1);
        Iterator<Map.Entry<Integer, OreWidget>> iterator = cache.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, OreWidget> entry = iterator.next();
            if (entry.getKey() < first || entry.getKey() > last) {
                iterator.remove();
            }
        }
        rows.clear();
        firstIndex = first;
        for (int i = first; i <= last; i++) {
            OreWidget row = cache.get(i);
            if (row == null) {
                row = factory.create(context, items.get(i));
                if (row == null) {
                    throw new IllegalStateException("row factory returned null");
                }
                cache.put(i, row);
                row.setOverlayManager(overlayManager());
            }
            rows.add(row);
        }
    }

    private OreWidget rowAt(float y) {
        int index = firstIndex + (int) ((y - bounds().y() + scroll) / rowHeight);
        int offset = index - firstIndex;
        return offset >= 0 && offset < rows.size() ? rows.get(offset) : null;
    }

    private float clampScroll(float value) {
        float content = items.size() * rowHeight;
        float height = bounds().height() > 0f ? bounds().height() : content;
        return Math.max(0f, Math.min(Math.max(0f, content - height), value));
    }
}