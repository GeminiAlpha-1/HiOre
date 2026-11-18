package dev.oreui.adapter262;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public final class Fabric26Pipelines {
    private static final Logger LOG = LoggerFactory.getLogger("oreui-adapter");
    private static final Identifier SHADER = Identifier.fromNamespaceAndPath("oreui", "core/ore_sdf");
    private static final Identifier LOCATION = Identifier.fromNamespaceAndPath("oreui", "sdf_ui");
    public static final RenderPipeline SDF = build();

    private Fabric26Pipelines() {
    }

    public static void initialize() {
        LOG.info("[oreui] sdf pipeline sortKey={}", SDF.getSortKey());
    }

    private static RenderPipeline build() {
        VertexFormat format = VertexFormat.builder(0)
                .addAttribute("Position", GpuFormat.RGB32_FLOAT)
                .addAttribute("Color", GpuFormat.RGBA8_UNORM)
                .addAttribute("UV0", GpuFormat.RG32_FLOAT)
                .addAttribute("UV1", GpuFormat.RG16_SINT)
                .addAttribute("UV2", GpuFormat.RG16_SINT)
                .build();

        RenderPipeline base = RenderPipelines.GUI;
        RenderPipeline.Builder builder = RenderPipeline.builder()
                .withLocation(LOCATION)
                .withVertexShader(SHADER)
                .withFragmentShader(SHADER)
                .withVertexBinding(0, format)
                .withColorTargetState(base.getColorTargetState())
                .withDepthStencilState(Optional.ofNullable(base.getDepthStencilState()))
                .withCull(base.isCull())
                .withPolygonMode(base.getPolygonMode())
                .withPrimitiveTopology(base.getPrimitiveTopology());
        for (BindGroupLayout layout : base.getBindGroupLayouts()) {
            builder.withBindGroupLayout(layout);
        }
        return builder.build();
    }
}