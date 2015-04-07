package net.kingdomsofarden.townships.api.effects;

import net.kingdomsofarden.townships.api.regions.Region;

/**
 * Denotes an Effect that is Tickable - i.e. performs a repeating set of actions every tick
 */
public interface TickableEffect extends Effect {

    /**
     * @return The time in milliseconds for the last tick of this effect, or -1 if not ticked yet
     */
    long lastTick();

    /**
     * @return The delay in milliseconds for which to schedule the next tick: if dynamic, this will be called right after a tick
     * to determine when to execute the next tick
     */
    long period();

    /**
     * Called when a region performs a tick on its effects
     * @param region The region performing the ticking
     * @param time The time in milliseconds the effect is being ticked
     * @return The time to schedule for the next tick, or LONG.MAX_VALUE to not schedule again
     */
    long onTick(Region region, long time);

}
