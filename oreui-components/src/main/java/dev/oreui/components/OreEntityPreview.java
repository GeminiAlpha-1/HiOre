package dev.oreui.components;

import dev.oreui.components.platform.IGameObjectRenderer;
import dev.oreui.components.platform.IGameObjectRenderer.IEntityHandle;
import dev.oreui.components.platform.IRenderContext;
import dev.oreui.core.geom.Rect2f;
import dev.oreui.theme.ColorRole;

public final class OreEntityPreview extends OreWidget {
    private static final float DAMPING = 0.15f;
    private static final float DRAG_SENSITIVITY = 0.5f;
    private static final float BORDER = 1f;

    private final Rect2f entityRect = new Rect2f();
    private IEntityHandle entity;
    private float yaw;
    private float pitch;
    private float targetYaw;
    private float targetPitch;
    private boolean dragging;
    private float lastX;

    public OreEntityPreview(ComponentContext context) {
        super(context);
        style().width(40f).height(40f);
    }

    public IEntityHandle entity() {
        return entity;
    }

    public void setEntity(IEntityHandle entity) {
        this.entity = entity;
    }

    public float yaw() {
        return yaw;
    }

    public float pitch() {
        return pitch;
    }

    public void setView(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.targetYaw = yaw;
        this.targetPitch = pitch;
    }

    public boolean isDragging() {
        return dragging;
    }

    @Override
    public boolean pointerDown(float x, float y) {
        if (!contains(x, y)) {
            return false;
        }
        dragging = true;
        lastX = x;
        return true;
    }

    @Override
    public boolean pointerMove(float x, float y) {
        if (!dragging) {
            return false;
        }
        targetYaw += (x - lastX) * DRAG_SENSITIVITY;
        lastX = x;
        return true;
    }

    @Override
    public boolean pointerUp(float x, float y) {
        if (!dragging) {
            return false;
        }
        dragging = false;
        return true;
    }

    @Override
    public void update(float deltaSeconds) {
        yaw += (targetYaw - yaw) * DAMPING;
        pitch += (targetPitch - pitch) * DAMPING;
        super.update(deltaSeconds);
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

        IGameObjectRenderer renderer = context.gameObjectRenderer();
        if (renderer != null && entity != null) {
            entityRect.set(x + BORDER, y + BORDER, w - BORDER * 2f, h - BORDER * 2f);
            renderer.submitEntity(entity, entityRect, yaw, pitch);
        }
    }
}