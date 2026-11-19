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
        float y = bounds().y() + 7f;
        float w = bounds().width();
        float fillWidth = Math.max(0f, (w - 2f) * value);

        fill(ctx, ColorRole.BORDER_OUTER, x, y, w, 6f);
        fill(ctx, ColorRole.SLIDER_TRACK_HIGHLIGHT, x + 1f, y + 1f, w - 2f, 2f);
        fill(ctx, ColorRole.SLIDER_TRACK, x + 1f, y + 3f, w - 2f, 2f);
        if (progressStyle == ProgressStyle.INDETERMINATE) {
            float period = Motion.INDETERMINATE_PERIOD.millis() / 1000f;
            float sweep = (elapsed % period) / period;
            float segment = 24f;
            float segmentX = x + 1f + (w - segment - 2f) * sweep;
            fill(ctx, ColorRole.ACCENT_HIGHLIGHT, segmentX, y + 1f, segment, 2f);
            fill(ctx, ColorRole.ACCENT_DEFAULT, segmentX, y + 3f, segment, 2f);
        } else {
            fill(ctx, ColorRole.ACCENT_HIGHLIGHT, x + 1f, y + 1f, fillWidth, 2f);
            fill(ctx, ColorRole.ACCENT_DEFAULT, x + 1f, y + 3f, fillWidth, 2f);
            if (progressStyle == ProgressStyle.SEGMENTED) {
                for (float sx = x + 8f; sx < x + w - 2f; sx += 8f) {
                    fill(ctx, ColorRole.SURFACE_SUNKEN, sx, y + 1f, 1f, 4f);
                }
            }
        }
    }

    @Override
    public void renderForeground(IRenderContext ctx) {
        if (text == null || progressStyle == ProgressStyle.CONTINUOUS) {
            return;
        }
        TextMetrics metrics = context.textMeasurer().measure(text, TypeRole.CAPTION, 0f);
        text(ctx, text, bounds().x() + (bounds().width() - metrics.width()) * 0.5f,
                textCenterY(bounds().y(), bounds().height(), metrics),
                TypeRole.CAPTION, ColorRole.TEXT_PRIMARY);
    }
}