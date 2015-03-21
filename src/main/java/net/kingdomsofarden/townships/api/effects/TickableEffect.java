package net.kingdomsofarden.townships.api.effects;

import net.kingdomsofarden.townships.api.regions.Region;

/**
 * Denotes an Effect that is Tickable - i.e. performs a repeating set of actions every tick
 */
public interface TickableEffect extends Effect {

    /**
     * @return The time in milliseconds to execute the first tick, or -1 if right away
     */
    public long startTime();

    /**
     * @return The delay in ticks for which to schedule the next tick: if dynamic, this will be called right after a tick
     * to determine when to execute the next tick
     */
    public long delay();

    /**
     * Called when a region performs a tick on its effects
     * @param region The region performing the ticking
     */
    public void onTick(Region region);

}
