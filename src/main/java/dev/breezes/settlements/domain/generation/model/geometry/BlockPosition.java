package dev.breezes.settlements.domain.generation.model.geometry;

public record BlockPosition(int x, int y, int z) {

    public static BlockPosition of(int x, int y, int z) {
        return new BlockPosition(x, y, z);
    }

    public double distanceTo(BlockPosition other) {
        long dx = (long) other.x - this.x;
        long dy = (long) other.y - this.y;
        long dz = (long) other.z - this.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public int manhattanDistanceTo(BlockPosition other) {
        return Math.abs(other.x - this.x)
                + Math.abs(other.y - this.y)
                + Math.abs(other.z - this.z);
    }

    public BlockPosition offset(int dx, int dy, int dz) {
        return new BlockPosition(this.x + dx, this.y + dy, this.z + dz);
    }

    public BlockPosition withY(int y) {
        return new BlockPosition(this.x, y, this.z);
    }

    public boolean sameXZ(BlockPosition other) {
        return this.x() == other.x() && this.z() == other.z();
    }

}
