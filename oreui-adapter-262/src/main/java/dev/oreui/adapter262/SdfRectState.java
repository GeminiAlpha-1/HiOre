package dev.oreui.adapter262;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import org.joml.Matrix3x2fc;

final class SdfRectState implements GuiElementRenderState {
    private final RenderPipeline pipeline;
    private final Matrix3x2fc pose;
    private final float x0;
    private final float y0;
    private final float x1;
    private final float y1;
    private final int color;
    private final int radius;
    private final int border;
    private final ScreenRectangle bounds;
    private final ScreenRectangle scissor;

    SdfRectState(RenderPipeline pipeline, Matrix3x2fc pose,
                 float x0, float y0, float x1, float y1,
                 int color, int radius, int border, ScreenRectangle scissor) {
        this.pipeline = pipeline;
        this.pose = pose;
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
        this.color = color;
        this.radius = radius;
        this.border = border;
        this.bounds = new ScreenRectangle(Math.round(x0), Math.round(y0),
                Math.round(x1 - x0), Math.round(y1 - y0)).transformMaxBounds(pose);
        this.scissor = scissor;
    }

    @Override
    public void buildVertices(VertexConsumer consumer) {
        int width = Math.round(x1 - x0);
        int height = Math.round(y1 - y0);
        consumer.addVertexWith2DPose(pose, x0, y0).setColor(color)
                .setUv(0f, 0f).setUv1(radius, border).setUv2(width, height);
        consumer.addVertexWith2DPose(pose, x0, y1).setColor(color)
                .setUv(0f, height).setUv1(radius, border).setUv2(width, height);
        consumer.addVertexWith2DPose(pose, x1, y1).setColor(color)
                .setUv(width, height).setUv1(radius, border).setUv2(width, height);
        consumer.addVertexWith2DPose(pose, x1, y0).setColor(color)
                .setUv(width, 0f).setUv1(radius, border).setUv2(width, height);
    }

    @Override
    public RenderPipeline pipeline() {
        return pipeline;
    }

    @Override
    public TextureSetup textureSetup() {
        return TextureSetup.noTexture();
    }

    @Override
    public ScreenRectangle scissorArea() {
        return scissor;
    }

    @Override
    public ScreenRectangle bounds() {
        return bounds;
    }
}