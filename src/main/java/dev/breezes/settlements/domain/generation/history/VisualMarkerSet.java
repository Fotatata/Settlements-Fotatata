package dev.breezes.settlements.domain.generation.history;

import java.util.HashSet;
import java.util.Set;

public record VisualMarkerSet(Set<String> markers) {

    public static final VisualMarkerSet EMPTY = new VisualMarkerSet(Set.of());

    public VisualMarkerSet {
        markers = markers == null ? Set.of() : markers;
    }

    public VisualMarkerSet merge(VisualMarkerSet other) {
        if (other == null || other.isEmpty()) {
            return this;
        }
        if (this.isEmpty()) {
            return other;
        }

        Set<String> merged = new HashSet<>(this.markers);
        merged.addAll(other.markers);
        return new VisualMarkerSet(merged);
    }

    public boolean contains(String marker) {
        return this.markers.contains(marker);
    }

    public boolean isEmpty() {
        return this.markers.isEmpty();
    }

}
