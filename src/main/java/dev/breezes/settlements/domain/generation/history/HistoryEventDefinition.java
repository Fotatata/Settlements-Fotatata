package dev.breezes.settlements.domain.generation.history;

import dev.breezes.settlements.domain.generation.model.profile.TraitId;
import lombok.Builder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Builder
public record HistoryEventDefinition(
        String id,
        String category,
        int timeHorizonMin,
        int timeHorizonMax,
        List<String> exclusiveTags,
        float probabilityWeight,
        EventPreconditions preconditions,
        Map<TraitId, Float> traitModifiers,
        List<String> visualMarkers,
        String narrativeText
) {

    public HistoryEventDefinition {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("History event id must not be blank");
        }
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("History event category must not be blank");
        }
        if (timeHorizonMax < timeHorizonMin) {
            throw new IllegalArgumentException("timeHorizonMax must be >= timeHorizonMin");
        }
        if (probabilityWeight <= 0.0f) {
            throw new IllegalArgumentException("probabilityWeight must be > 0");
        }
        exclusiveTags = List.copyOf(exclusiveTags == null ? List.of() : exclusiveTags);
        preconditions = preconditions == null ? EventPreconditions.NONE : preconditions;
        traitModifiers = Map.copyOf(traitModifiers == null ? Map.of() : new LinkedHashMap<>(traitModifiers));
        visualMarkers = List.copyOf(visualMarkers == null ? List.of() : visualMarkers);
        if (narrativeText == null || narrativeText.isBlank()) {
            throw new IllegalArgumentException("History event narrativeText must not be blank");
        }
    }

}
