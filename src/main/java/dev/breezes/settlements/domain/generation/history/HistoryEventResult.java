package dev.breezes.settlements.domain.generation.history;

import dev.breezes.settlements.domain.generation.model.profile.TraitId;
import lombok.Builder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Builder
public record HistoryEventResult(
        List<String> eventIds,
        Map<TraitId, Float> modifiedWeights,
        VisualMarkerSet visualMarkers
) {

    public static final HistoryEventResult EMPTY = new HistoryEventResult(List.of(), Map.of(), VisualMarkerSet.EMPTY);

    public HistoryEventResult {
        eventIds = eventIds == null ? List.of() : eventIds;
        modifiedWeights = modifiedWeights == null ? Map.of() : new LinkedHashMap<>(modifiedWeights);
        visualMarkers = visualMarkers == null ? VisualMarkerSet.EMPTY : visualMarkers;
    }

}
