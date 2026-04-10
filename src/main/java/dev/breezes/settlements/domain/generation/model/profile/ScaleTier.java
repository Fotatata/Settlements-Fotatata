package dev.breezes.settlements.domain.generation.model.profile;

public enum ScaleTier {

    HAMLET(1, 2, 10, 20, 1, 2, 5, 8, 32),
    VILLAGE(2, 4, 30, 60, 2, 4, 10, 20, 56),
    TOWN(4, 5, 80, 150, 3, 5, 25, 40, 80);

    private final int minTraitSlots;
    private final int maxTraitSlots;
    private final int minPopulation;
    private final int maxPopulation;
    private final int minHistoryEvents;
    private final int maxHistoryEvents;
    private final int minBuildings;
    private final int maxBuildings;
    private final int areaRadius;

    ScaleTier(int minTraitSlots, int maxTraitSlots, int minPopulation, int maxPopulation,
              int minHistoryEvents, int maxHistoryEvents, int minBuildings, int maxBuildings,
              int areaRadius) {
        this.minTraitSlots = minTraitSlots;
        this.maxTraitSlots = maxTraitSlots;
        this.minPopulation = minPopulation;
        this.maxPopulation = maxPopulation;
        this.minHistoryEvents = minHistoryEvents;
        this.maxHistoryEvents = maxHistoryEvents;
        this.minBuildings = minBuildings;
        this.maxBuildings = maxBuildings;
        this.areaRadius = areaRadius;
    }

    public int minTraitSlots() {
        return this.minTraitSlots;
    }

    public int maxTraitSlots() {
        return this.maxTraitSlots;
    }

    public int minPopulation() {
        return this.minPopulation;
    }

    public int maxPopulation() {
        return this.maxPopulation;
    }

    public int minHistoryEvents() {
        return this.minHistoryEvents;
    }

    public int maxHistoryEvents() {
        return this.maxHistoryEvents;
    }

    public int minBuildings() {
        return this.minBuildings;
    }

    public int maxBuildings() {
        return this.maxBuildings;
    }

    public int areaRadius() {
        return this.areaRadius;
    }

}
