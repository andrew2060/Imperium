package net.kingdomsofarden.townships.api.effects;

import net.kingdomsofarden.townships.api.ITownshipsPlugin;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.util.StoredDataSection;

public interface Effect {
    /**
     * @return The name of the effect
     */
    String getName();

    /**
     * Called when the effect is initialized for the first time
     * @param plugin The initializing plugin
     */
    void onInit(ITownshipsPlugin plugin);

    /**
     * @param plugin The plugin loading this effect
     * @param region The region containing this effect
     * @param data Any data associated with this effect
     */
    void onLoad(ITownshipsPlugin plugin, Region region, StoredDataSection data);

    /**
     * @param plugin The plugin unloading this effect
     * @param region The region containing this effect
     * @param data Any data associated with this effect to be saved
     */
    void onUnload(ITownshipsPlugin plugin, Region region, StoredDataSection data);

    /**
     * @return The region associated with this (that has this particular) effect
     */
    Region getRegion();
}
