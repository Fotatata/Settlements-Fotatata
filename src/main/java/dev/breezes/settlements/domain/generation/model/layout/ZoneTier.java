package dev.breezes.settlements.domain.generation.model.layout;

public enum ZoneTier {
    CORE(0),
    DOWNTOWN(1),
    MIDTOWN(2),
    OUTER(3),
    SUBURB(4);

    private final int rank;

    ZoneTier(int rank) {
        this.rank = rank;
    }

    public int rank() {
        return this.rank;
    }
}
