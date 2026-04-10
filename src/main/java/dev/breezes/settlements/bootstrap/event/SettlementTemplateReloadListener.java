package dev.breezes.settlements.bootstrap.event;

import dev.breezes.settlements.infrastructure.minecraft.worldgen.NbtTemplateResolver;
import lombok.CustomLog;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import javax.annotation.Nonnull;

@CustomLog
public final class SettlementTemplateReloadListener extends SimplePreparableReloadListener<Void> {

    private static final SettlementTemplateReloadListener INSTANCE = new SettlementTemplateReloadListener();

    public static SettlementTemplateReloadListener getInstance() {
        return INSTANCE;
    }

    @Override
    protected Void prepare(@Nonnull ResourceManager resourceManager, @Nonnull ProfilerFiller profiler) {
        return null;
    }

    @Override
    protected void apply(@Nonnull Void ignored,
                         @Nonnull ResourceManager resourceManager,
                         @Nonnull ProfilerFiller profiler) {
        log.resourceLoadingStatus("Refreshing settlement template resolver after data reload");
        NbtTemplateResolver.refresh(resourceManager);
    }

}
