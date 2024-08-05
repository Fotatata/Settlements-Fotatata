package dev.breezes.settlements.models.misc;

/**
 * Represents a tickable that is permanent and does not have a duration
 */
public class PermanentTickable implements ITickable {

    @Override
    public void tick(long delta) {
        // Do nothing
    }

    @Override
    public void reset() {
        // Do nothing
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public String getRemainingCooldownsAsPrettyString() {
        return "∞";
    }

}
