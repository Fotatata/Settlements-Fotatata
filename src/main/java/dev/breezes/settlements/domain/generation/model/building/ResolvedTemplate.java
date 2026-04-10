package dev.breezes.settlements.domain.generation.model.building;

import lombok.Builder;

import java.util.Set;

@Builder
public record ResolvedTemplate(
        String templatePath,
        int width,
        int depth,
        Set<String> tags
) {

    public ResolvedTemplate {
        if (templatePath == null || templatePath.isBlank()) {
            throw new IllegalArgumentException("templatePath must not be blank");
        }
        if (width <= 0 || depth <= 0) {
            throw new IllegalArgumentException("width and depth must be > 0");
        }
        tags = tags == null ? Set.of() : tags;
    }

}
