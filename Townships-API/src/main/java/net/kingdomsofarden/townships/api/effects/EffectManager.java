package net.kingdomsofarden.townships.api.effects;

import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.util.StoredDataSection;

/**
 * Manages loading, scheduling, and creation of new effect instances
 */
public interface EffectManager {
    /**
     * Loads the given effect
     * @param name The effect to load
     * @param region The region loading the effect
     * @param config A representation of the stored effect configuration, likely part of the region configuration
     *               itself, or a newly created one containing default values for dynamically loaded effects
     * @return The loaded effect
     */
    Effect loadEffect(String name, Region region, StoredDataSection config);
}
