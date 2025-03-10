package dev.breezes.settlements.models.behaviors.stages;

import dev.breezes.settlements.models.behaviors.StopBehaviorException;
import dev.breezes.settlements.models.behaviors.states.BehaviorContext;
import dev.breezes.settlements.models.behaviors.steps.AbstractStep;
import dev.breezes.settlements.models.behaviors.steps.BehaviorStep;
import lombok.Builder;
import lombok.CustomLog;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@CustomLog
public class StagedStep extends AbstractStep {

    private final Map<Stage, BehaviorStep> stageStepMap;
    private final Stage startingStage;
    private final Stage initialActionStage;
    private final Stage nextStage;

    @Getter
    private Stage currentStage;

    @Builder
    private StagedStep(@Nonnull String name, @Nonnull Map<Stage, BehaviorStep> stageStepMap, @Nonnull Stage initialStage, @Nonnull Stage nextStage) {
        super("StagedStep[%s]".formatted(name));
        if (stageStepMap.isEmpty()) {
            throw new IllegalArgumentException("Staged step must have at least one stage");
        }

        this.stageStepMap = new HashMap<>(stageStepMap);
        this.initialActionStage = initialStage;
        this.nextStage = nextStage;

        this.startingStage = ControlStages.newStepStartStage();
        this.stageStepMap.put(this.startingStage, this::onStart);

        this.currentStage = this.startingStage;
    }

    protected Optional<Stage> onStart(@Nonnull BehaviorContext context) {
        log.behaviorStatus("Starting %s (%s)".formatted(this.getName(), this.getUuid()));
        return Optional.of(this.initialActionStage);
    }

    public Optional<Stage> tick(@Nonnull BehaviorContext context) {
        BehaviorStep step = this.stageStepMap.get(this.currentStage);
        Optional<Stage> nextStage = step.tick(context);

        // Case 1: the inner step is not yet complete
        if (nextStage.isEmpty()) {
            return Optional.empty();
        }
        // Case 2: the inner step is complete and a fall-through is requested
        else if (nextStage.get() == ControlStages.STEP_END) {
            this.transitionStage(ControlStages.STEP_END);
            this.onEnd(context);
            return Optional.of(this.nextStage);
        }
        // Case 3: the inner step is complete and a specific stage is requested
        else {
            this.transitionStage(nextStage.get());
            return Optional.empty();
        }
    }

    protected void onEnd(@Nonnull BehaviorContext context) {
        log.behaviorStatus("Ending %s (%s)".formatted(this.getName(), this.getUuid()));
    }

    public void reset() {
        this.currentStage = this.startingStage;
    }

    private void transitionStage(@Nonnull Stage stage) {
        // Special case: ending staged step
        if (stage == ControlStages.STEP_END) {
            log.behaviorStatus("End requested for %s (%s)".formatted(this.getName(), this.getUuid()));
            this.currentStage = ControlStages.STEP_END;
            return;
        }

        if (!this.stageStepMap.containsKey(stage)) {
            log.warn("Attempted to transition to an unknown stage: %s for %s (%s)".formatted(stage.toString(), this.getName(), this.getUuid()));
            throw new StopBehaviorException("Attempted to transition to an unknown stage: %s".formatted(stage.toString()));
        }

        log.behaviorStatus("Transitioning from %s to %s stage for %s (%s)"
                .formatted(this.currentStage.getName(), stage.getName(), this.getName(), this.getUuid()));
        this.currentStage = stage;
    }

}
