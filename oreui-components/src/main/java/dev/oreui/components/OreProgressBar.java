package dev.oreui.components;

import dev.oreui.components.platform.IRenderContext;
import dev.oreui.components.platform.IText;
import dev.oreui.components.platform.TextMetrics;
import dev.oreui.theme.ColorRole;
import dev.oreui.theme.Motion;
import dev.oreui.theme.TypeRole;

public final class OreProgressBar extends OreWidget {
    private final ProgressStyle progressStyle;
    private float value;
    private float elapsed;
    private IText text;

    public OreProgressBar(ComponentContext context, ProgressStyle progressStyle) {
        super(context);
        this.progressStyle = progressStyle;
        style().width(180f).height(20f);
    }

    public void setValue(float value) {
        this.value = Math.max(0f, Math.min(1f, value));
    }

    public float value() {
        return value;
    }

    public void setText(IText text) {
        this.text = text;
    }

    @Override
    public void update(float deltaSeconds) {
        elapsed += deltaSeconds;
        super.update(deltaSeconds);
    }

    @Override
    public void renderBackground(IRenderContext ctx) {
        float x = bounds().x();
        float y = bounds().y() + 6f;
        float w = bounds().width();
        float h = 8f;
        fill(ctx, ColorRole.SURFACE_SUNKEN, x, y, w, h);
        outline(ctx, ColorRole.BORDER_OUTER, x, y, w, h, 1f);
        if (progressStyle == ProgressStyle.INDETERMINATE) {
            float period = Motion.INDETERMINATE_PERIOD.millis() / 1000f;
            float sweep = (elapsed % period) / period;
            float segment = 24f;
            float segmentX = x + 1f + (w - segment - 2f) * sweep;
            fill(ctx, ColorRole.PROGRESS_FILL, segmentX, y + 1f, segment, h - 2f);
            fill(ctx, ColorRole.BEVEL_LIGHT, segmentX, y + 1f, segment, 1f);
        } else {
            float fillWidth = Math.max(0f, (w - 2f) * value);
            fill(ctx, ColorRole.PROGRESS_FILL, x + 1f, y + 1f, fillWidth, h - 2f);
            fill(ctx, ColorRole.BEVEL_LIGHT, x + 1f, y + 1f, fillWidth, 1f);
            fill(ctx, ColorRole.PROGRESS_FILL_LIP, x + 1f, y + h - 2f, fillWidth, 1f);
            if (progressStyle == ProgressStyle.SEGMENTED) {
                for (float sx = x + 8f; sx < x + w - 2f; sx += 8f) {
                    fill(ctx, ColorRole.SURFACE_SUNKEN, sx, y + 1f, 2f, h - 2f);
                }
            }
        }
    }

    @Override
    public void renderForeground(IRenderContext ctx) {
        if (text == null) {
            return;
        }
        TextMetrics metrics = context.textMeasurer().measure(text, TypeRole.CAPTION, 0f);
        text(ctx, text, bounds().x() + (bounds().width() - metrics.width()) * 0.5f,
                textCenterY(bounds().y(), bounds().height(), metrics),
                TypeRole.CAPTION, ColorRole.TEXT_PRIMARY);
    }
}