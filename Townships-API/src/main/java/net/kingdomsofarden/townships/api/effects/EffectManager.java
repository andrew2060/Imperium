package net.kingdomsofarden.townships.api.effects;

import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.util.StoredDataSection;

/**
 * Manages loading, scheduling, and creation of new effect instances
 */
public interface EffectManager {
    Effect loadEffect(String name, Region region, StoredDataSection config);
}
