package dev.breezes.settlements.domain.generation.building;

import dev.breezes.settlements.domain.generation.history.VisualMarkerSet;

import java.util.HashSet;
import java.util.Set;

public record TemplateResolutionContext(
        Set<String> biomeTags,
        VisualMarkerSet visualMarkers
) {

    public static final TemplateResolutionContext EMPTY =
            new TemplateResolutionContext(Set.of(), VisualMarkerSet.EMPTY);

    public TemplateResolutionContext {
        biomeTags = biomeTags == null ? Set.of() : biomeTags;
        visualMarkers = visualMarkers == null ? VisualMarkerSet.EMPTY : visualMarkers;
    }

    public Set<String> allRequestedTags() {
        Set<String> all = new HashSet<>(this.biomeTags);
        all.addAll(this.visualMarkers.markers());
        return Set.copyOf(all);
    }

}
