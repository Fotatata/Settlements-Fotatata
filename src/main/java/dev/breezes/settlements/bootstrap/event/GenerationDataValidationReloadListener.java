package dev.breezes.settlements.bootstrap.event;

import dev.breezes.settlements.infrastructure.minecraft.data.building.BuildingDefinitionDataManager;
import dev.breezes.settlements.infrastructure.minecraft.data.scoring.TraitScorerDataManager;
import dev.breezes.settlements.infrastructure.minecraft.data.traits.TraitDefinitionDataManager;
import dev.breezes.settlements.infrastructure.minecraft.data.validation.GenerationDataValidator;
import lombok.CustomLog;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import javax.annotation.Nonnull;

@CustomLog
public final class GenerationDataValidationReloadListener extends SimplePreparableReloadListener<Void> {

    private static final GenerationDataValidationReloadListener INSTANCE = new GenerationDataValidationReloadListener();

    private final GenerationDataValidator validator = new GenerationDataValidator();

    public static GenerationDataValidationReloadListener getInstance() {
        return INSTANCE;
    }

    @Override
    protected Void prepare(@Nonnull ResourceManager resourceManager, @Nonnull ProfilerFiller profiler) {
        return null;
    }

    @Override
    protected void apply(Void ignored, @Nonnull ResourceManager resourceManager, @Nonnull ProfilerFiller profiler) {
        log.info("Validating generation datapack cross-registry references");
        this.validator.validateAndApply(
                TraitDefinitionDataManager.getInstance(),
                TraitScorerDataManager.getInstance(),
                BuildingDefinitionDataManager.getInstance()
        );
    }

}
