package dev.oreui.components;

import dev.oreui.components.platform.IRenderContext;
import dev.oreui.theme.ColorRole;
import dev.oreui.theme.Motion;

public final class OreSpinner extends OreWidget {
    private float angle;

    public OreSpinner(ComponentContext context) {
        super(context);
        style().width(16f).height(16f);
    }

    @Override
    public void update(float deltaSeconds) {
        float period = Motion.INDETERMINATE_PERIOD.millis() / 1000f;
        angle += deltaSeconds * (float) (Math.PI * 2.0) / period;
    }

    @Override
    public void renderBackground(IRenderContext ctx) {
        float cx = bounds().x() + 8f;
        float cy = bounds().y() + 8f;
        for (int i = 0; i < 8; i++) {
            double a = angle + i * Math.PI / 4.0;
            float x = cx + (float) Math.cos(a) * 5f;
            float y = cy + (float) Math.sin(a) * 5f;
            fill(ctx, i == 0 ? ColorRole.ICON_PRIMARY : ColorRole.ICON_MUTED, x, y, 2f, 2f);
        }
    }
}