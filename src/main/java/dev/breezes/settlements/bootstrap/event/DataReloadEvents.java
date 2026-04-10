package dev.breezes.settlements.bootstrap.event;

import dev.breezes.settlements.SettlementsMod;
import dev.breezes.settlements.infrastructure.minecraft.data.building.BuildingDefinitionDataManager;
import dev.breezes.settlements.infrastructure.minecraft.data.enchanting.EnchantmentCostDataManager;
import dev.breezes.settlements.infrastructure.minecraft.data.enchanting.SpecializationDataManager;
import dev.breezes.settlements.infrastructure.minecraft.data.fishing.FishCatchDataManager;
import dev.breezes.settlements.infrastructure.minecraft.data.history.HistoryEventDataManager;
import dev.breezes.settlements.infrastructure.minecraft.data.scoring.TraitScorerDataManager;
import dev.breezes.settlements.infrastructure.minecraft.data.survey.BiomeSurveyDataManager;
import dev.breezes.settlements.infrastructure.minecraft.data.traits.TraitDefinitionDataManager;
import lombok.CustomLog;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

@EventBusSubscriber(modid = SettlementsMod.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
@CustomLog
public class DataReloadEvents {

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        log.info("{}: reloading all data", SettlementsMod.MOD_NAME);

        event.addListener(EnchantmentCostDataManager.getInstance());
        event.addListener(SpecializationDataManager.getInstance());
        event.addListener(FishCatchDataManager.getInstance());
        event.addListener(BiomeSurveyDataManager.getInstance());
        event.addListener(TraitDefinitionDataManager.getInstance());
        event.addListener(TraitScorerDataManager.getInstance());
        event.addListener(HistoryEventDataManager.getInstance());
        event.addListener(BuildingDefinitionDataManager.getInstance());
        event.addListener(GenerationDataValidationReloadListener.getInstance());
        event.addListener(SettlementTemplateReloadListener.getInstance());
    }

}
