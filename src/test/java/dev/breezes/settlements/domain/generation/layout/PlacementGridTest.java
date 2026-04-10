package dev.breezes.settlements.domain.generation.layout;

import dev.breezes.settlements.domain.generation.model.geometry.BlockPosition;
import dev.breezes.settlements.domain.generation.model.geometry.BoundingRegion;
import dev.breezes.settlements.domain.generation.model.layout.RoadSegment;
import dev.breezes.settlements.domain.generation.model.layout.RoadType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlacementGridTest {

    @Test
    void hasConflict_respectsMinimumSpacing() {
        PlacementGrid grid = new PlacementGrid(4);
        grid.occupy(BoundingRegion.of(new BlockPosition(0, 64, 0), new BlockPosition(4, 68, 4)));

        assertTrue(grid.hasConflict(BoundingRegion.of(new BlockPosition(7, 64, 0), new BlockPosition(11, 68, 4))));
        assertFalse(grid.hasConflict(BoundingRegion.of(new BlockPosition(9, 64, 0), new BlockPosition(13, 68, 4))));
    }

    @Test
    void roadBounds_canActAsPaddingBarrier() {
        PlacementGrid grid = new PlacementGrid(4);
        grid.occupy(BoundingRegion.of(new BlockPosition(0, 64, 0), new BlockPosition(4, 68, 4)));
        LayoutSupport.rasterizeRoad(new RoadSegment(
                new BlockPosition(6, 64, -1),
                new BlockPosition(6, 64, 6),
                RoadType.MAIN
        )).forEach(grid::occupyRoad);

        assertFalse(grid.hasConflict(BoundingRegion.of(new BlockPosition(10, 64, 0), new BlockPosition(14, 68, 4))));
    }

}
