package dev.oreui.adapter262;

import dev.oreui.components.platform.IGameObjectRenderer;
import dev.oreui.core.geom.Rect2f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.entity.Entity;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Fabric26GameObjectRenderer implements IGameObjectRenderer {
    private static final Logger LOG = LoggerFactory.getLogger("oreui-adapter");
    private static final float UNIT_SCALE = 1f;
    private static final float GUI_ENTITY_SCALE = 30f;
    private static final float PI = (float) Math.PI;
    private static final float DEG_TO_RAD = (float) Math.PI / 180f;


    private final Vector3f origin = new Vector3f();
    private final Quaternionf rotation = new Quaternionf();
    private final Quaternionf pitchRotation = new Quaternionf();
    private GuiGraphicsExtractor extractor;

    void bind(GuiGraphicsExtractor extractor) {
        this.extractor = extractor;
    }

    void unbind() {
        this.extractor = null;
    }

    @Override
    public void submitItem(IItemHandle handle, Rect2f rect, float scale) {
        if (!(handle instanceof Fabric26ItemHandle item)) {
            LOG.warn("[oreui] unsupported item handle: {}",
                    handle == null ? "null" : handle.getClass().getName());
            return;
        }
        GuiGraphicsExtractor target = requireExtractor();
        target.pose().pushMatrix();
        target.pose().translate(rect.x(), rect.y());
        if (scale != UNIT_SCALE) {
            target.pose().scale(scale, scale);
        }
        target.item(item.stack(), 0, 0);
        target.pose().popMatrix();
    }

    @Override
    public void submitFluid(IFluidHandle handle, Rect2f rect) {
        if (!(handle instanceof Fabric26FluidHandle fluid)) {
            LOG.warn("[oreui] unsupported fluid handle: {}",
                    handle == null ? "null" : handle.getClass().getName());
            return;
        }
        requireExtractor().blitSprite(RenderPipelines.GUI_TEXTURED, fluid.sprite(),
                Math.round(rect.x()), Math.round(rect.y()),
                Math.round(rect.width()), Math.round(rect.height()));
    }

    @Override
    public void submitEntity(IEntityHandle handle, Rect2f rect, float yaw, float pitch) {
        if (!(handle instanceof Fabric26EntityHandle entityHandle)) {
            LOG.warn("[oreui] unsupported entity handle: {}",
                    handle == null ? "null" : handle.getClass().getName());
            return;
        }
        if (rect.width() < 1f || rect.height() < 1f) {
            return;
        }
        GuiGraphicsExtractor target = requireExtractor();
        Entity entity = entityHandle.entity();
        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        EntityRenderer<? super Entity, ?> renderer = dispatcher.getRenderer(entity);
        EntityRenderState state = renderer.createRenderState(entity, 1f);
        state.shadowPieces.clear();
        state.outlineColor = 0;
        state.nameTag = null;
        state.scoreText = null;
        state.leashStates = null;
        state.passengerOffset = null;
        state.lightCoords = LightCoordsUtil.FULL_BRIGHT;

        if (state instanceof LivingEntityRenderState living) {
            float bodyRotation = 180f - yaw * 20f;
            living.bodyRot = bodyRotation;
            living.yRot = 0f;
            living.xRot = -pitch * 20f;
            float modelScale = living.scale > 0f ? living.scale : 1f;
            living.boundingBoxWidth /= modelScale;
            living.boundingBoxHeight /= modelScale;
            living.eyeHeight /= modelScale;
            living.scale = 1f;
        }

        origin.set(0f, state.boundingBoxHeight * 0.5f, 0f);
        pitchRotation.identity().rotateX(pitch * 20f * DEG_TO_RAD);
        rotation.identity().rotateZ(PI).mul(pitchRotation);
        float oreScale = OreGuiScale.renderScale();
        int left = Math.round(rect.x() * oreScale);
        int top = Math.round(rect.y() * oreScale);
        int right = left + Math.round(rect.width() * oreScale);
        int bottom = top + Math.round(rect.height() * oreScale);
        target.entity(state, GUI_ENTITY_SCALE, origin, rotation, pitchRotation,
                left, top, right, bottom);
    }

    private GuiGraphicsExtractor requireExtractor() {
        if (extractor == null) {
            throw new IllegalStateException("game object renderer is not bound to a render pass");
        }
        return extractor;
    }
}