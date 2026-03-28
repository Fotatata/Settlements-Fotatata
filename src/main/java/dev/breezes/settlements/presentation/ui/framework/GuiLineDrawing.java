package dev.breezes.settlements.presentation.ui.framework;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.breezes.settlements.shared.annotations.functional.ClientSide;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;

import javax.annotation.Nonnull;

/**
 * Tesselator-based line and polygon drawing for GUI rendering.
 * Uses OpenGL primitives (DEBUG_LINES, TRIANGLE_FAN) for efficient batched rendering
 * instead of per-pixel fill calls.
 */
@ClientSide
public final class GuiLineDrawing {

    /**
     * Draws a single line segment between two points.
     */
    public static void drawLine(@Nonnull GuiGraphics graphics, float x1, float y1, float x2, float y2, int color) {
        float a = (color >> 24 & 0xFF) / 255.0F;
        float r = (color >> 16 & 0xFF) / 255.0F;
        float g = (color >> 8 & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;

        Matrix4f matrix = graphics.pose().last().pose();
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        builder.addVertex(matrix, x1, y1, 0).setColor(r, g, b, a);
        builder.addVertex(matrix, x2, y2, 0).setColor(r, g, b, a);
        MeshData meshData = builder.buildOrThrow();
        BufferUploader.drawWithShader(meshData);

        RenderSystem.disableBlend();
    }

    /**
     * Draws a closed polygon outline through the given vertices.
     */
    public static void drawPolygonOutline(@Nonnull GuiGraphics graphics, float[][] vertices, int color) {
        if (vertices.length < 2) {
            return;
        }

        float a = (color >> 24 & 0xFF) / 255.0F;
        float r = (color >> 16 & 0xFF) / 255.0F;
        float g = (color >> 8 & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;

        Matrix4f matrix = graphics.pose().last().pose();
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        for (int i = 0; i < vertices.length; i++) {
            int next = (i + 1) % vertices.length;
            builder.addVertex(matrix, vertices[i][0], vertices[i][1], 0).setColor(r, g, b, a);
            builder.addVertex(matrix, vertices[next][0], vertices[next][1], 0).setColor(r, g, b, a);
        }
        MeshData meshData = builder.buildOrThrow();
        BufferUploader.drawWithShader(meshData);

        RenderSystem.disableBlend();
    }

    /**
     * Fills a convex polygon using TRIANGLE_FAN from the centroid.
     */
    public static void fillConvexPolygon(@Nonnull GuiGraphics graphics, float[][] vertices, int color) {
        if (vertices.length < 3) {
            return;
        }

        // Compute centroid as fan origin
        float cx = 0, cy = 0;
        for (float[] vertex : vertices) {
            cx += vertex[0];
            cy += vertex[1];
        }
        cx /= vertices.length;
        cy /= vertices.length;

        fillTriangleFan(graphics, cx, cy, vertices, color);
    }

    /**
     * Fills a star-convex polygon using TRIANGLE_FAN from an explicit center point.
     * <p>
     * Star-convex means every vertex is visible from the center without crossing an edge.
     * This is always true for radar/spider chart polygons where each vertex lies on a ray
     * from the chart center at fixed angular intervals, regardless of how concave the
     * polygon's outline is.
     */
    public static void fillStarPolygon(@Nonnull GuiGraphics graphics,
                                       float centerX, float centerY,
                                       float[][] vertices, int color) {
        if (vertices.length < 3) {
            return;
        }
        fillTriangleFan(graphics, centerX, centerY, vertices, color);
    }

    private static void fillTriangleFan(@Nonnull GuiGraphics graphics,
                                        float fanX, float fanY,
                                        float[][] vertices, int color) {
        float a = (color >> 24 & 0xFF) / 255.0F;
        float r = (color >> 16 & 0xFF) / 255.0F;
        float g = (color >> 8 & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;

        Matrix4f matrix = graphics.pose().last().pose();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
        builder.addVertex(matrix, fanX, fanY, 0).setColor(r, g, b, a);
        for (float[] vertex : vertices) {
            builder.addVertex(matrix, vertex[0], vertex[1], 0).setColor(r, g, b, a);
        }
        // Close the fan by repeating the first vertex
        builder.addVertex(matrix, vertices[0][0], vertices[0][1], 0).setColor(r, g, b, a);
        MeshData meshData = builder.buildOrThrow();
        BufferUploader.drawWithShader(meshData);

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    /**
     * Computes the 6 vertices of a regular hexagon centered at (cx, cy) with the given radius.
     * Vertex 0 is at the top (12 o'clock), proceeding clockwise.
     */
    public static float[][] computeHexagonVertices(float cx, float cy, float radius) {
        float[][] vertices = new float[6][2];
        for (int i = 0; i < 6; i++) {
            // Start at -90 degrees (top) and go clockwise in 60-degree steps
            double angle = Math.toRadians(-90 + i * 60);
            vertices[i][0] = cx + radius * (float) Math.cos(angle);
            vertices[i][1] = cy + radius * (float) Math.sin(angle);
        }
        return vertices;
    }

    /**
     * Draws a regular hexagon outline centered at (cx, cy).
     */
    public static void drawHexagon(@Nonnull GuiGraphics graphics, float cx, float cy, float radius, int color) {
        float[][] vertices = computeHexagonVertices(cx, cy, radius);
        drawPolygonOutline(graphics, vertices, color);
    }

}
