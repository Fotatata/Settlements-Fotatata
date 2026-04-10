package dev.breezes.settlements.domain.generation.history;

import java.util.List;

public interface HistoryEventRegistry {

    List<HistoryEventDefinition> allEvents();

}
