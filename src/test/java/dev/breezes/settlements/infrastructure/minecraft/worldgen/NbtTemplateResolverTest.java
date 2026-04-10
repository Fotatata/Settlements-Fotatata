package dev.breezes.settlements.infrastructure.minecraft.worldgen;

import dev.breezes.settlements.domain.generation.building.TemplateResolutionContext;
import dev.breezes.settlements.domain.generation.history.VisualMarkerSet;
import dev.breezes.settlements.domain.generation.model.IntRange;
import dev.breezes.settlements.domain.generation.model.building.BuildingDefinition;
import dev.breezes.settlements.domain.generation.model.building.FootprintConstraint;
import dev.breezes.settlements.domain.generation.model.building.ResolvedTemplate;
import dev.breezes.settlements.domain.generation.model.profile.TraitSlot;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NbtTemplateResolverTest {

    @Test
    void fullMatch_beatsPartialAndUntaggedFallback() {
        NbtTemplateResolver resolver = resolverWithEntries(List.of(
                entry("settlements:house_plain", "settlements:house", 5, 5, Set.of()),
                entry("settlements:house_taiga", "settlements:house", 5, 5, Set.of("taiga")),
                entry("settlements:house_taiga_charred", "settlements:house", 5, 5, Set.of("taiga", "charred"))
        ));

        Optional<ResolvedTemplate> resolved = resolver.resolve(
                building("settlements:house", Set.of()),
                new Random(1L),
                new TemplateResolutionContext(Set.of("taiga"), new VisualMarkerSet(Set.of("charred")))
        );

        assertEquals("settlements:house_taiga_charred", resolved.orElseThrow().templatePath());
    }

    @Test
    void partialMatch_usesHighestOverlapGroup() {
        NbtTemplateResolver resolver = resolverWithEntries(List.of(
                entry("settlements:house_plain", "settlements:house", 5, 5, Set.of()),
                entry("settlements:house_taiga", "settlements:house", 5, 5, Set.of("taiga")),
                entry("settlements:house_charred", "settlements:house", 5, 5, Set.of("charred"))
        ));

        Optional<ResolvedTemplate> resolved = resolver.resolve(
                building("settlements:house", Set.of("taiga", "snowy")),
                new Random(2L),
                TemplateResolutionContext.EMPTY
        );

        assertEquals("settlements:house_taiga", resolved.orElseThrow().templatePath());
    }

    @Test
    void noRequestedTags_prefersUntaggedFallback() {
        NbtTemplateResolver resolver = resolverWithEntries(List.of(
                entry("settlements:house_plain", "settlements:house", 5, 5, Set.of()),
                entry("settlements:house_taiga", "settlements:house", 5, 5, Set.of("taiga"))
        ));

        Optional<ResolvedTemplate> resolved = resolver.resolve(
                building("settlements:house", Set.of()),
                new Random(3L),
                TemplateResolutionContext.EMPTY
        );

        assertEquals("settlements:house_plain", resolved.orElseThrow().templatePath());
    }

    @Test
    void noTagMatch_fallsBackToUntagged() {
        NbtTemplateResolver resolver = resolverWithEntries(List.of(
                entry("settlements:house_plain", "settlements:house", 5, 5, Set.of()),
                entry("settlements:house_taiga", "settlements:house", 5, 5, Set.of("taiga"))
        ));

        Optional<ResolvedTemplate> resolved = resolver.resolve(
                building("settlements:house", Set.of()),
                new Random(4L),
                new TemplateResolutionContext(Set.of("desert"), VisualMarkerSet.EMPTY)
        );

        assertEquals("settlements:house_plain", resolved.orElseThrow().templatePath());
    }

    @Test
    void missingBuildingCatalog_returnsEmpty() {
        NbtTemplateResolver resolver = new NbtTemplateResolver(Map.of(
                "settlements:watchtower", List.of(entry("settlements:watchtower_plain", "settlements:watchtower", 5, 5, Set.of()))
        ));

        assertTrue(resolver.resolve(building("settlements:house", Set.of()), new Random(5L), TemplateResolutionContext.EMPTY).isEmpty());
    }

    @Test
    void footprintFiltering_appliesBeforeTagSelection() {
        NbtTemplateResolver resolver = resolverWithEntries(List.of(
                entry("settlements:house_large", "settlements:house", 9, 9, Set.of("taiga")),
                entry("settlements:house_small", "settlements:house", 5, 5, Set.of())
        ));

        Optional<ResolvedTemplate> resolved = resolver.resolve(
                building("settlements:house", Set.of()),
                new Random(6L),
                new TemplateResolutionContext(Set.of("taiga"), VisualMarkerSet.EMPTY)
        );

        assertEquals("settlements:house_small", resolved.orElseThrow().templatePath());
    }

    @Test
    void toMinecraftTemplateId_stripsNeoForgeStructureRootAndNbtExtension() {
        ResourceLocation resourcePath = ResourceLocation.parse("settlements:structure/buildings/fish_market.nbt");
        ResourceLocation templateId = NbtTemplateResolver.toMinecraftTemplateId(resourcePath);

        assertEquals("settlements:buildings/fish_market", templateId.toString());
    }

    @Test
    void toTemplateId_preservesNestedPathWithinMinecraftTemplateIdentifier() {
        ResourceLocation resourcePath = ResourceLocation.parse("settlements:structure/buildings/coastal/fish_market.nbt");
        ResourceLocation templateId = NbtTemplateResolver.toMinecraftTemplateId(resourcePath);

        assertEquals("settlements:buildings/coastal/fish_market", templateId.toString());
    }

    @Test
    void toMinecraftTemplateId_rejectsUnexpectedStructureResourcePath() {
        ResourceLocation resourcePath = ResourceLocation.parse("settlements:buildings/fish_market.nbt");

        assertThrows(IllegalStateException.class, () -> NbtTemplateResolver.toMinecraftTemplateId(resourcePath));
    }

    @Test
    void materializeCatalogForTesting_buildsEntriesFromReloadedDescriptors() {
        List<NbtTemplateResolver.TemplateDescriptor> descriptors = List.of(
                descriptor("settlements:buildings/house_plain", "settlements:house", Set.of()),
                descriptor("settlements:buildings/house_taiga", "settlements:house", Set.of("taiga")),
                descriptor("settlements:buildings/watchtower", "settlements:watchtower", Set.of())
        );

        Map<String, List<NbtTemplateResolver.TemplateEntry>> catalog = NbtTemplateResolver.materializeCatalogForTesting(
                descriptors,
                templateId -> switch (templateId.toString()) {
                    case "settlements:buildings/house_plain" -> Optional.of(new Vec3i(5, 4, 5));
                    case "settlements:buildings/house_taiga" -> Optional.of(new Vec3i(5, 4, 5));
                    case "settlements:buildings/watchtower" -> Optional.of(new Vec3i(7, 10, 7));
                    default -> Optional.empty();
                }
        );

        assertEquals(2, catalog.size());
        assertEquals(2, catalog.get("settlements:house").size());
        assertEquals(7, catalog.get("settlements:watchtower").getFirst().width());
    }

    @Test
    void materializeCatalogForTesting_skipsDescriptorsWithoutUsableTemplateSize() {
        List<NbtTemplateResolver.TemplateDescriptor> descriptors = List.of(
                descriptor("settlements:buildings/house_plain", "settlements:house", Set.of()),
                descriptor("settlements:buildings/house_broken", "settlements:house", Set.of("taiga")),
                descriptor("settlements:buildings/watchtower_missing", "settlements:watchtower", Set.of())
        );

        Map<String, List<NbtTemplateResolver.TemplateEntry>> catalog = NbtTemplateResolver.materializeCatalogForTesting(
                descriptors,
                templateId -> switch (templateId.toString()) {
                    case "settlements:buildings/house_plain" -> Optional.of(new Vec3i(5, 4, 5));
                    case "settlements:buildings/house_broken" -> Optional.of(new Vec3i(0, 4, 5));
                    default -> Optional.empty();
                }
        );

        assertEquals(1, catalog.size());
        assertEquals(1, catalog.get("settlements:house").size());
        assertTrue(catalog.getOrDefault("settlements:watchtower", List.of()).isEmpty());
    }

    private static NbtTemplateResolver resolverWithEntries(List<NbtTemplateResolver.TemplateEntry> entries) {
        return new NbtTemplateResolver(Map.of("settlements:house", entries));
    }

    private static NbtTemplateResolver.TemplateEntry entry(String templateId,
                                                           String buildingDefinitionId,
                                                           int width,
                                                           int depth,
                                                           Set<String> tags) {
        return new NbtTemplateResolver.TemplateEntry(
                ResourceLocation.parse(templateId),
                buildingDefinitionId,
                width,
                depth,
                tags
        );
    }

    private static NbtTemplateResolver.TemplateDescriptor descriptor(String templateId,
                                                                     String buildingDefinitionId,
                                                                     Set<String> tags) {
        return NbtTemplateResolver.TemplateDescriptor.builder()
                .templateId(ResourceLocation.parse(templateId))
                .buildingDefinitionId(buildingDefinitionId)
                .tags(tags)
                .author(null)
                .build();
    }

    private static BuildingDefinition building(String id, Set<String> preferredTags) {
        return BuildingDefinition.builder()
                .id(id)
                .displayInfo(null)
                .traitAffinities(Map.of())
                .minimumRank(TraitSlot.FLAVOR)
                .placementPriority(1)
                .zoneTierPreference(IntRange.of(0, 4))
                .requiresRoadFrontage(false)
                .requiresResources(Set.of())
                .forbiddenResources(Set.of())
                .footprint(new FootprintConstraint(4, 6, 4, 6))
                .preferredTags(preferredTags)
                .proximityAffinities(List.of())
                .globalAffinities(List.of())
                .npcProfession(null)
                .npcCount(0)
                .build();
    }

}
