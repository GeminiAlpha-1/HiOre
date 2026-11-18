package dev.oreui.components;

import dev.oreui.components.platform.IGameObjectRenderer;
import dev.oreui.components.platform.IGameObjectRenderer.IItemHandle;
import dev.oreui.components.platform.IRenderContext;
import dev.oreui.components.platform.IText;
import dev.oreui.core.color.ColorMath;
import dev.oreui.core.geom.Rect2f;
import dev.oreui.theme.ColorRole;
import dev.oreui.theme.ControlState;
import dev.oreui.theme.Space;
import dev.oreui.theme.TypeRole;

public final class OreItemSlot extends OreInteractive {
    private static final float BORDER = 1f;
    private static final float INNER_PAD = 1f;
    private static final float ITEM_TEXTURE_SIZE = 16f;

    private final SlotStyle slotStyle;
    private final Rect2f itemRect = new Rect2f();
    private final Rect2f damageRect = new Rect2f();
    private IItemHandle item;
    private boolean empty = true;
    private int count;
    private boolean damageable;
    private int durability;
    private int durabilityMax;
    private IText countText;
    private float countWidth;

    public OreItemSlot(ComponentContext context, SlotStyle slotStyle) {
        super(context);
        this.slotStyle = slotStyle;
        style().width(slotStyle.outer()).height(slotStyle.outer());
    }

    public SlotStyle slotStyle() {
        return slotStyle;
    }

    public IItemHandle item() {
        return item;
    }

    public void setItem(IItemHandle item) {
        this.item = item;
        refresh();
    }

    public int count() {
        return count;
    }

    @Override
    public void update(float deltaSeconds) {
        refresh();
        super.update(deltaSeconds);
    }

    private void refresh() {
        if (item == null) {
            empty = true;
            count = 0;
            damageable = false;
            durability = 0;
            durabilityMax = 0;
            countText = null;
            countWidth = 0f;
            return;
        }
        empty = item.isEmpty();
        int nextCount = empty ? 0 : item.count();
        if (nextCount != count) {
            count = nextCount;
            countText = count > 0 ? new LiteralText(Integer.toString(count)) : null;
            countWidth = countText == null ? 0f
                    : context.textMeasurer().measure(countText, TypeRole.CAPTION, 0f).width();
        }
        damageable = !empty && item.isDamageable();
        durability = damageable ? item.durability() : 0;
        durabilityMax = damageable ? item.durabilityMax() : 0;
    }

    @Override
    public void renderBackground(IRenderContext ctx) {
        float x = bounds().x();
        float y = bounds().y();
        float w = bounds().width();
        float h = bounds().height();
        fill(ctx, ColorRole.SURFACE_SUNKEN, x, y, w, h);
        fill(ctx, ColorRole.WELL_SHADOW, x, y, w, Space.XXS.value());
        fill(ctx, ColorRole.WELL_RIM, x, y + h - BORDER, w, BORDER);
        outline(ctx, ColorRole.BORDER_OUTER, x, y, w, h, BORDER);
        if (state() == ControlState.HOVER) {
            fill(ctx, ColorRole.SLOT_HOVER, x + BORDER, y + BORDER, w - BORDER * 2f, h - BORDER * 2f);
        }
        drawItem(ctx);
        drawDamage(ctx);
        super.renderBackground(ctx);
        drawFocus(ctx);
    }

    @Override
    public void renderForeground(IRenderContext ctx) {
        if (showsCount() && countText != null) {
            float lineHeight = context.theme().type(TypeRole.CAPTION).lineHeight();
            float x = bounds().right() - countWidth - INNER_PAD;
            float y = bounds().bottom() - lineHeight - INNER_PAD;
            text(ctx, countText, x, y, TypeRole.CAPTION, ColorRole.TEXT_PRIMARY);
        }
        super.renderForeground(ctx);
    }

    private boolean showsCount() {
        return !empty && count > 1 && slotStyle != SlotStyle.OUTPUT_ONLY
                && slotStyle != SlotStyle.GHOST_FILTER;
    }

    private void drawItem(IRenderContext ctx) {
        if (empty || slotStyle == SlotStyle.GHOST_FILTER) {
            return;
        }
        IGameObjectRenderer renderer = context.gameObjectRenderer();
        if (renderer == null) {
            return;
        }
        float inset = (slotStyle.outer() - slotStyle.inner()) * 0.5f;
        itemRect.set(bounds().x() + inset, bounds().y() + inset, slotStyle.inner(), slotStyle.inner());
        renderer.submitItem(item, itemRect, slotStyle.inner() / ITEM_TEXTURE_SIZE);
    }

    private void drawDamage(IRenderContext ctx) {
        if (!damageable || durabilityMax <= 0 || slotStyle == SlotStyle.GHOST_FILTER) {
            return;
        }
        float ratio = Math.max(0f, Math.min(1f, (float) durability / (float) durabilityMax));
        int argb = ColorMath.lerp(color(ColorRole.DANGER_DEFAULT), color(ColorRole.ACCENT_DEFAULT), ratio);
        float width = bounds().width() - BORDER * 2f;
        damageRect.set(bounds().x() + BORDER,
                bounds().bottom() - BORDER - Space.XXS.value(),
                width * ratio, Space.XXS.value());
        ctx.submitPixelRect(damageRect, argb);
    }
}