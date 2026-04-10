package dev.breezes.settlements.domain.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class BiomeId {

    private static final Map<String, BiomeId> POOL = new ConcurrentHashMap<>();

    private final String namespace;
    private final String path;
    private final String full;

    private BiomeId(String namespace, String path) {
        this.namespace = namespace;
        this.path = path;
        this.full = namespace + ":" + path;
    }

    public static BiomeId of(String full) {
        if (full == null || full.isBlank()) {
            throw new IllegalArgumentException("Biome id must not be null or blank");
        }

        return POOL.computeIfAbsent(full, key -> {
            int colon = key.indexOf(':');
            if (colon < 0) {
                throw new IllegalArgumentException("Missing ':' in " + key);
            }
            if (colon == 0 || colon == key.length() - 1) {
                throw new IllegalArgumentException("Biome id must be in 'namespace:path' form: " + key);
            }
            return new BiomeId(key.substring(0, colon), key.substring(colon + 1));
        });
    }

    public String namespace() {
        return this.namespace;
    }

    public String path() {
        return this.path;
    }

    public String full() {
        return this.full;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return this.full;
    }

}
