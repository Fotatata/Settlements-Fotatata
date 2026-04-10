package dev.breezes.settlements.domain.generation.model.geometry;

public enum Direction {

    NORTH,
    SOUTH,
    EAST,
    WEST;

    public Direction opposite() {
        return switch (this) {
            case NORTH -> SOUTH;
            case SOUTH -> NORTH;
            case EAST -> WEST;
            case WEST -> EAST;
        };
    }

    public Direction rotateClockwise() {
        return switch (this) {
            case NORTH -> EAST;
            case EAST -> SOUTH;
            case SOUTH -> WEST;
            case WEST -> NORTH;
        };
    }

    public Direction rotateCounterClockwise() {
        return switch (this) {
            case NORTH -> WEST;
            case WEST -> SOUTH;
            case SOUTH -> EAST;
            case EAST -> NORTH;
        };
    }

    public int offsetX() {
        return switch (this) {
            case EAST -> 1;
            case WEST -> -1;
            case NORTH, SOUTH -> 0;
        };
    }

    public int offsetZ() {
        return switch (this) {
            case SOUTH -> 1;
            case NORTH -> -1;
            case EAST, WEST -> 0;
        };
    }

}
