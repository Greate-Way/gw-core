package dev.diberv.gwCore.client.lidar;


import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Optional;
import java.util.OptionalDouble;

public class PointRenderer {
    private static final StagedVertexBuffer stagedBuffer =
            new StagedVertexBuffer(
                    () -> "My Points Buffer",
                    RenderType.SMALL_BUFFER_SIZE
            );


    public static void onInitializeClient() {
        LevelRenderEvents.AFTER_TRANSLUCENT_TERRAIN.register(context -> {
            RenderPipeline pipeline = RenderPipelines.DEBUG_FILLED_BOX;

            VertexFormat format = pipeline.getVertexFormatBinding(0);
            PrimitiveTopology topology = pipeline.getPrimitiveTopology();

            StagedVertexBuffer.Draw draw = stagedBuffer.appendDraw(
                    format,
                    topology,
                    RenderSystem.getProjectionType().vertexSorting()
            );

            VertexConsumer buffer = stagedBuffer.getVertexBuilder(draw);
            PoseStack matrices = context.poseStack();

            var camera = context.levelState().cameraRenderState.pos;

            matrices.pushPose();
            matrices.translate(-camera.x, -camera.y, -camera.z);

            Matrix4fc matrix = matrices.last().pose();

            PointManager.getPoints().forEach(point -> {
                renderPoint(
                        matrix, buffer,
                        point.x, point.y, point.z,
                        0.05f,
                        point.r, point.g, point.b, 1f
                );
            });

            stagedBuffer.upload();

            StagedVertexBuffer.ExecuteInfo info =
                    stagedBuffer.getExecuteInfo(draw);

            if (info != null) {
                draw(Minecraft.getInstance(), info, pipeline);
            }

            stagedBuffer.endFrame();
        });
    }




    private static void renderPoint(
            Matrix4fc matrix,
            VertexConsumer buffer,
            float x, float y, float z,
            float radius,
            float r, float g, float b, float a
    ) {
        renderFilledBox(
                matrix, buffer,
                x - radius, y - radius, z - radius,
                x + radius, y + radius, z + radius,
                r, g, b, a
        );
    }



    private static void renderFilledBox(
            Matrix4fc matrix,
            VertexConsumer buffer,
            float minX, float minY, float minZ,
            float maxX, float maxY, float maxZ,
            float r, float g, float b, float a
    ) {
        // Нижняя грань
        buffer.addVertex(matrix, minX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, minY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, minY, maxZ).setColor(r, g, b, a);

        // Верхняя грань
        buffer.addVertex(matrix, minX, maxY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, maxY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, maxY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, maxY, minZ).setColor(r, g, b, a);

        // Передняя грань
        buffer.addVertex(matrix, minX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, maxY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, maxY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, minY, minZ).setColor(r, g, b, a);

        // Задняя грань
        buffer.addVertex(matrix, minX, minY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, minY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, maxY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, maxY, maxZ).setColor(r, g, b, a);

        // Левая грань
        buffer.addVertex(matrix, minX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, minY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, maxY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, maxY, minZ).setColor(r, g, b, a);

        // Правая грань
        buffer.addVertex(matrix, maxX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, maxY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, maxY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, minY, maxZ).setColor(r, g, b, a);
    }






    private static final Vector4f COLOR_MODULATOR =
            new Vector4f(1f, 1f, 1f, 1f);

    private static final Vector3f MODEL_OFFSET =
            new Vector3f();

    private static final Matrix4f TEXTURE_MATRIX =
            new Matrix4f();

    private static void draw(
            Minecraft client,
            StagedVertexBuffer.ExecuteInfo info,
            RenderPipeline pipeline
    ) {
        GpuBufferSlice dynamicTransforms =
                RenderSystem.getDynamicUniforms().writeTransform(
                        RenderSystem.getModelViewMatrixCopy(),
                        COLOR_MODULATOR,
                        MODEL_OFFSET,
                        TEXTURE_MATRIX
                );

        RenderTarget mainTarget =
                client.gameRenderer.mainRenderTarget();

        GpuTextureView colorTexture =
                mainTarget.getColorTextureView();

        assert colorTexture != null;

        try (RenderPass renderPass =
                     RenderSystem.getDevice()
                             .createCommandEncoder()
                             .createRenderPass(
                                     () -> "gwcore waypoint rendering",
                                     colorTexture,
                                     Optional.empty(),
                                     mainTarget.getDepthTextureView(),
                                     OptionalDouble.empty()
                             )) {

            renderPass.setPipeline(pipeline);

            RenderSystem.bindDefaultUniforms(renderPass);

            renderPass.setUniform(
                    "DynamicTransforms",
                    dynamicTransforms
            );

            renderPass.setVertexBuffer(
                    0,
                    info.vertexBuffer().slice()
            );

            renderPass.setIndexBuffer(
                    info.indexBuffer(),
                    info.indexType()
            );

            renderPass.drawIndexed(
                    info.indexCount(),
                    1,
                    info.firstIndex(),
                    info.baseVertex(),
                    0
            );
        }
    }
}
