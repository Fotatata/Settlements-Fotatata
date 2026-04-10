package dev.breezes.settlements.domain.generation.model.building;

import lombok.Builder;

@Builder
public record FootprintConstraint(
        int minWidth,
        int maxWidth,
        int minDepth,
        int maxDepth
) {

    public FootprintConstraint {
        if (minWidth <= 0 || maxWidth <= 0 || minDepth <= 0 || maxDepth <= 0) {
            throw new IllegalArgumentException("Footprint dimensions must be > 0");
        }
        if (maxWidth < minWidth || maxDepth < minDepth) {
            throw new IllegalArgumentException("Maximum footprint dimensions must be >= minimum dimensions");
        }
    }

    public boolean fits(int actualWidth, int actualDepth) {
        return actualWidth >= this.minWidth && actualWidth <= this.maxWidth
                && actualDepth >= this.minDepth && actualDepth <= this.maxDepth;
    }

}
