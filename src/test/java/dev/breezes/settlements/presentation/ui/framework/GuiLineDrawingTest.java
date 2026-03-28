package dev.breezes.settlements.presentation.ui.framework;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class GuiLineDrawingTest {

    @Test
    void computeHexagonVertices_returnsCorrectCount() {
        float[][] vertices = GuiLineDrawing.computeHexagonVertices(0, 0, 50);
        Assertions.assertEquals(6, vertices.length);
    }

    @Test
    void computeHexagonVertices_vertex0AtTop() {
        float cx = 100, cy = 100, radius = 40;
        float[][] vertices = GuiLineDrawing.computeHexagonVertices(cx, cy, radius);

        // Vertex 0 is at -90° (top), so x = cx, y = cy - radius
        Assertions.assertEquals(cx, vertices[0][0], 0.01F);
        Assertions.assertEquals(cy - radius, vertices[0][1], 0.01F);
    }

    @Test
    void computeHexagonVertices_vertex3AtBottom() {
        float cx = 100, cy = 100, radius = 40;
        float[][] vertices = GuiLineDrawing.computeHexagonVertices(cx, cy, radius);

        // Vertex 3 is at 90° (bottom), so x = cx, y = cy + radius
        Assertions.assertEquals(cx, vertices[3][0], 0.01F);
        Assertions.assertEquals(cy + radius, vertices[3][1], 0.01F);
    }

    @Test
    void computeHexagonVertices_allVerticesAtCorrectDistance() {
        float cx = 50, cy = 75, radius = 30;
        float[][] vertices = GuiLineDrawing.computeHexagonVertices(cx, cy, radius);

        for (int i = 0; i < 6; i++) {
            float dx = vertices[i][0] - cx;
            float dy = vertices[i][1] - cy;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            Assertions.assertEquals(radius, distance, 0.01F, "Vertex " + i + " distance from center");
        }
    }

    @Test
    void computeHexagonVertices_adjacentVertices60DegreesApart() {
        float cx = 0, cy = 0, radius = 100;
        float[][] vertices = GuiLineDrawing.computeHexagonVertices(cx, cy, radius);

        // Each pair of adjacent vertices should be 60 degrees apart
        for (int i = 0; i < 6; i++) {
            int next = (i + 1) % 6;
            double angle1 = Math.atan2(vertices[i][1] - cy, vertices[i][0] - cx);
            double angle2 = Math.atan2(vertices[next][1] - cy, vertices[next][0] - cx);

            double diff = angle2 - angle1;
            // Normalize to [0, 2pi)
            if (diff < 0) {
                diff += 2 * Math.PI;
            }
            Assertions.assertEquals(Math.PI / 3, diff, 0.01,
                    "Angle between vertex " + i + " and " + next + " should be 60°");
        }
    }

    @Test
    void computeHexagonVertices_zeroRadius_allVerticesAtCenter() {
        float cx = 50, cy = 50;
        float[][] vertices = GuiLineDrawing.computeHexagonVertices(cx, cy, 0);

        for (int i = 0; i < 6; i++) {
            Assertions.assertEquals(cx, vertices[i][0], 0.01F);
            Assertions.assertEquals(cy, vertices[i][1], 0.01F);
        }
    }

}
