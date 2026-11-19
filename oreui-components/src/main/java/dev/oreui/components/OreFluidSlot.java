package dev.oreui.components;

import dev.oreui.components.platform.IGameObjectRenderer;
import dev.oreui.components.platform.IGameObjectRenderer.IFluidHandle;
import dev.oreui.components.platform.IRenderContext;
import dev.oreui.core.geom.Rect2f;
import dev.oreui.theme.ColorRole;

public final class OreFluidSlot extends OreWidget {
    private static final float BORDER = 1f;
    private static final float GAUGE_LONG = 0.4f;
    private static final float GAUGE_SHORT = 0.2f;

    private final Rect2f fluidRect = new Rect2f();
    private IFluidHandle fluid;
    private float level;

    public OreFluidSlot(ComponentContext context) {
        super(context);
        style().width(18f).height(18f);
    }

    public IFluidHandle fluid() {
        return fluid;
    }

    public void setFluid(IFluidHandle fluid) {
        this.fluid = fluid;
    }

    public float level() {
        return level;
    }

    public void setLevel(float level) {
        this.level = Math.max(0f, Math.min(1f, level));
    }

    @Override
    public void renderBackground(IRenderContext ctx) {
        float x = bounds().x();
        float y = bounds().y();
        float w = bounds().width();
        float h = bounds().height();
        fill(ctx, ColorRole.SURFACE_SUNKEN, x, y, w, h);
        fill(ctx, ColorRole.WELL_SHADOW, x, y, w, 2f);
        fill(ctx, ColorRole.WELL_RIM, x, y + h - BORDER, w, BORDER);
        outline(ctx, ColorRole.BORDER_OUTER, x, y, w, h, BORDER);

        if (level > 0f) {
            float inner = h - BORDER * 2f;
            float fillHeight = inner * level;
            fill(ctx, ColorRole.PROGRESS_FILL, x + BORDER, y + h - BORDER - fillHeight,
                    w - BORDER * 2f, fillHeight);
            fill(ctx, ColorRole.PROGRESS_FILL_LIP, x + BORDER, y + h - BORDER - fillHeight,
                    w - BORDER * 2f, BORDER);
        }

        IGameObjectRenderer renderer = context.gameObjectRenderer();
        if (renderer != null && fluid != null) {
            fluidRect.set(x + BORDER, y + BORDER, w - BORDER * 2f, h - BORDER * 2f);
            renderer.submitFluid(fluid, fluidRect);
        }

        float gaugeX = x + BORDER;
        float gaugeTop = y + BORDER + 1f;
        float gaugeBottom = y + h - BORDER - 1f;
        int ticks = 4;
        for (int i = 0; i <= ticks; i++) {
            float ty = gaugeBottom - (gaugeBottom - gaugeTop) * i / ticks;
            float length = i == 0 || i == ticks ? 4f : 2f;
            fill(ctx, ColorRole.BORDER_OUTER, gaugeX, ty, length, 1f);
        }
    }

    @Override
    public void renderForeground(IRenderContext ctx) {
        super.renderForeground(ctx);
    }
}