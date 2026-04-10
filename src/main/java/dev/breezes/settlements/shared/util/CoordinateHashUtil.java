package dev.breezes.settlements.shared.util;

public final class CoordinateHashUtil {

    /**
     * Produces a well-distributed 64-bit hash from a 2D block/chunk coordinate pair.
     * Uses the MurmurHash3 finalizer mixing rounds to ensure good avalanche properties.
     * Suitable for XOR-ing with a world seed to derive a position-specific generation seed.
     */
    public static long hash(int x, int z) {
        long mixed = 341873128712L * x + 132897987541L * z;
        mixed ^= (mixed >>> 33);
        mixed *= 0xff51afd7ed558ccdL;
        mixed ^= (mixed >>> 33);
        mixed *= 0xc4ceb9fe1a85ec53L;
        mixed ^= (mixed >>> 33);
        return mixed;
    }

}
