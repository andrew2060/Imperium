package net.kingdomsofarden.townships.api.effects;

import net.kingdomsofarden.townships.api.regions.FunctionalRegion;

/**
 * Denotes an Effect that is Tickable - i.e. performs a repeating set of actions every tick
 */
public interface TickableEffect extends Effect {

    /**
     * @return The time in milliseconds for the first tick of this effect, or -1 to start immediately
     */
    long startTime();

    /**
     * Called when a region performs a tick on its effects
     *
     * @param region The region performing the ticking
     * @param time   The time in milliseconds the effect is being ticked
     * @return The time to schedule for the next tick, or LONG.MAX_VALUE to not schedule again
     */
    long onTick(FunctionalRegion region, long time);

}
