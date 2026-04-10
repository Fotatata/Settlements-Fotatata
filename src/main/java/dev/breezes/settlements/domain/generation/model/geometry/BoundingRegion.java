package dev.breezes.settlements.domain.generation.model.geometry;

public record BoundingRegion(BlockPosition min, BlockPosition max) {

    public BoundingRegion {
        if (min.x() > max.x() || min.y() > max.y() || min.z() > max.z()) {
            throw new IllegalArgumentException("BoundingRegion min must be <= max on all axes");
        }
    }

    public static BoundingRegion of(BlockPosition a, BlockPosition b) {
        return new BoundingRegion(
                new BlockPosition(Math.min(a.x(), b.x()), Math.min(a.y(), b.y()), Math.min(a.z(), b.z())),
                new BlockPosition(Math.max(a.x(), b.x()), Math.max(a.y(), b.y()), Math.max(a.z(), b.z()))
        );
    }

    public boolean contains(BlockPosition point) {
        return point.x() >= this.min.x() && point.x() <= this.max.x()
                && point.y() >= this.min.y() && point.y() <= this.max.y()
                && point.z() >= this.min.z() && point.z() <= this.max.z();
    }

    public boolean intersects(BoundingRegion other) {
        return this.min.x() <= other.max.x() && this.max.x() >= other.min.x()
                && this.min.y() <= other.max.y() && this.max.y() >= other.min.y()
                && this.min.z() <= other.max.z() && this.max.z() >= other.min.z();
    }

    public int widthX() {
        return this.max.x() - this.min.x() + 1;
    }

    public int widthZ() {
        return this.max.z() - this.min.z() + 1;
    }

    public int areaXZ() {
        return this.widthX() * this.widthZ();
    }

    public BlockPosition centerXZ() {
        return new BlockPosition(
                this.min.x() + (this.widthX() - 1) / 2,
                this.min.y(),
                this.min.z() + (this.widthZ() - 1) / 2
        );
    }

    public BoundingRegion expandedBy(int margin) {
        if (margin < 0) {
            throw new IllegalArgumentException("margin must be >= 0");
        }
        return new BoundingRegion(
                new BlockPosition(this.min.x() - margin, this.min.y(), this.min.z() - margin),
                new BlockPosition(this.max.x() + margin, this.max.y(), this.max.z() + margin)
        );
    }

    public BoundingRegion clampedTo(BoundingRegion other) {
        BlockPosition clampedMin = new BlockPosition(
                Math.max(this.min.x(), other.min.x()),
                Math.max(this.min.y(), other.min.y()),
                Math.max(this.min.z(), other.min.z())
        );
        BlockPosition clampedMax = new BlockPosition(
                Math.min(this.max.x(), other.max.x()),
                Math.min(this.max.y(), other.max.y()),
                Math.min(this.max.z(), other.max.z())
        );
        if (clampedMin.x() > clampedMax.x() || clampedMin.y() > clampedMax.y() || clampedMin.z() > clampedMax.z()) {
            throw new IllegalArgumentException("Bounding regions do not intersect; cannot clamp");
        }
        return new BoundingRegion(clampedMin, clampedMax);
    }

}
