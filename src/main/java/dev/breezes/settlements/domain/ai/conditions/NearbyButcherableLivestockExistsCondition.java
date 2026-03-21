package dev.breezes.settlements.domain.ai.conditions;

import dev.breezes.settlements.domain.tags.EntityTag;
import dev.breezes.settlements.infrastructure.minecraft.entities.villager.BaseVillager;
import lombok.Builder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class NearbyButcherableLivestockExistsCondition<T extends BaseVillager> implements IEntityCondition<T> {

    private final double rangeHorizontal;
    private final double rangeVertical;
    private final Map<EntityType<?>, Integer> minimumKeepByType;
    private final boolean requireVillageOwnedTag;

    @Nullable
    private Animal target;
    @Nullable
    private EntityType<?> selectedType;

    @Builder
    public NearbyButcherableLivestockExistsCondition(double rangeHorizontal,
                                                     double rangeVertical,
                                                     @Nonnull Map<EntityType<?>, Integer> minimumKeepByType,
                                                     boolean requireVillageOwnedTag) {
        this.rangeHorizontal = rangeHorizontal;
        this.rangeVertical = rangeVertical;
        this.minimumKeepByType = new HashMap<>(minimumKeepByType);
        this.requireVillageOwnedTag = requireVillageOwnedTag;

        this.target = null;
        this.selectedType = null;
    }

    @Override
    public boolean test(@Nullable T source) {
        this.target = null;
        this.selectedType = null;

        if (source == null) {
            return false;
        }

        List<Animal> animals = this.getMatchingNearbyAnimals(source);
        if (animals.isEmpty()) {
            return false;
        }

        Map<EntityType<?>, Integer> adultCountByType = new HashMap<>();
        Map<EntityType<?>, Animal> firstAdultByType = new HashMap<>();
        for (Animal animal : animals) {
            if (!animal.isAlive() || animal.isBaby()) {
                continue;
            }

            EntityType<?> type = animal.getType();
            adultCountByType.merge(type, 1, Integer::sum);
            firstAdultByType.putIfAbsent(type, animal);
        }

        // Pick the first candidate from any type that exceeds minimum keep count.
        for (Map.Entry<EntityType<?>, Integer> entry : adultCountByType.entrySet()) {
            EntityType<?> type = entry.getKey();
            int totalAdults = entry.getValue();

            int minimumKeep = this.minimumKeepByType.getOrDefault(type, Integer.MAX_VALUE);
            if (totalAdults <= minimumKeep) {
                continue;
            }

            Animal candidate = firstAdultByType.get(type);
            if (candidate != null) {
                this.target = candidate;
                this.selectedType = type;
                return true;
            }
        }

        return false;
    }

    public Optional<Animal> getTarget() {
        return Optional.ofNullable(this.target);
    }

    public Optional<EntityType<?>> getSelectedType() {
        return Optional.ofNullable(this.selectedType);
    }

    /**
     * Used only for finding butcher-ready targets of a specific entity type
     */
    public Optional<Animal> findTargetForType(@Nonnull T source,
                                              @Nonnull EntityType<?> desiredType) {
        if (!this.minimumKeepByType.containsKey(desiredType)) {
            return Optional.empty();
        }

        List<Animal> animals = this.getMatchingNearbyAnimals(source);
        if (animals.isEmpty()) {
            return Optional.empty();
        }

        int total = 0;
        List<Animal> adults = new ArrayList<>();
        for (Animal animal : animals) {
            if (animal.getType() != desiredType || !animal.isAlive() || animal.isBaby()) {
                continue;
            }

            total++;
            adults.add(animal);
        }

        int minimumKeep = this.minimumKeepByType.getOrDefault(desiredType, Integer.MAX_VALUE);
        if (total <= minimumKeep || adults.isEmpty()) {
            return Optional.empty();
        }

        adults.sort(Comparator.comparingDouble(source::distanceToSqr));
        return Optional.of(adults.getFirst());
    }

    private List<Animal> getMatchingNearbyAnimals(@Nonnull T source) {
        AABB scanBoundary = source.getBoundingBox().inflate(this.rangeHorizontal, this.rangeVertical, this.rangeHorizontal);
        Predicate<Animal> readyForButcheringPredicate = animal -> {
            if (animal == null || !animal.isAlive()) {
                return false;
            }
            if (!this.minimumKeepByType.containsKey(animal.getType())) {
                return false;
            }
            if (!this.requireVillageOwnedTag) {
                return true;
            }
            return animal.getTags().contains(EntityTag.VILLAGE_OWNED_ANIMAL.getTag());
        };

        return source.level().getEntitiesOfClass(Animal.class, scanBoundary, readyForButcheringPredicate);
    }

}
