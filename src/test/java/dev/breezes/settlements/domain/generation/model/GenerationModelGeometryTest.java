package dev.breezes.settlements.domain.generation.model;

import dev.breezes.settlements.domain.generation.model.geometry.BlockPosition;
import dev.breezes.settlements.domain.generation.model.geometry.BoundingRegion;
import dev.breezes.settlements.domain.generation.model.geometry.Direction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GenerationModelGeometryTest {

    @Test
    void blockPosition_distanceAndOffsetsBehaveAsExpected() {
        BlockPosition origin = new BlockPosition(0, 64, 0);
        BlockPosition target = new BlockPosition(3, 68, 4);

        assertEquals(5, origin.manhattanDistanceTo(new BlockPosition(2, 64, 3)));
        assertEquals(6.4031242374328485d, origin.distanceTo(target));
        assertEquals(new BlockPosition(1, 66, -1), origin.offset(1, 2, -1));
        assertEquals(new BlockPosition(0, 70, 0), origin.withY(70));
    }

    @Test
    void boundingRegion_containsIntersectsCenterAndClampBehaveAsExpected() {
        BoundingRegion region = BoundingRegion.of(new BlockPosition(0, 60, 0), new BlockPosition(4, 70, 6));
        BoundingRegion other = BoundingRegion.of(new BlockPosition(3, 65, 2), new BlockPosition(8, 80, 8));

        assertTrue(region.contains(new BlockPosition(2, 65, 4)));
        assertTrue(region.intersects(other));
        assertEquals(5, region.widthX());
        assertEquals(7, region.widthZ());
        assertEquals(35, region.areaXZ());
        assertEquals(new BlockPosition(2, 60, 3), region.centerXZ());
        assertEquals(BoundingRegion.of(new BlockPosition(1, 60, 1), new BlockPosition(3, 70, 5)),
                region.clampedTo(BoundingRegion.of(new BlockPosition(1, 50, 1), new BlockPosition(3, 75, 5))));
    }

    @Test
    void boundingRegion_clampThrowsWhenNoIntersection() {
        BoundingRegion a = BoundingRegion.of(new BlockPosition(0, 0, 0), new BlockPosition(1, 1, 1));
        BoundingRegion b = BoundingRegion.of(new BlockPosition(2, 2, 2), new BlockPosition(3, 3, 3));

        assertThrows(IllegalArgumentException.class, () -> a.clampedTo(b));
    }

    @Test
    void direction_rotationAndOffsetsBehaveAsExpected() {
        assertEquals(Direction.SOUTH, Direction.NORTH.opposite());
        assertEquals(Direction.EAST, Direction.NORTH.rotateClockwise());
        assertEquals(Direction.WEST, Direction.NORTH.rotateCounterClockwise());
        assertEquals(1, Direction.EAST.offsetX());
        assertEquals(-1, Direction.NORTH.offsetZ());
    }
}
