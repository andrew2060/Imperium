package net.kingdomsofarden.townships.api.effects;

import net.kingdomsofarden.townships.api.regions.Region;

public interface Effect {
    /**
     * Called when a region performs a tick on its effects
     * @param region The region performing the ticking
     */
    public void onTick(Region region);
}
