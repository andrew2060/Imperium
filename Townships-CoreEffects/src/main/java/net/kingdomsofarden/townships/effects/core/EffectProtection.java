package net.kingdomsofarden.townships.effects.core;

import net.kingdomsofarden.townships.api.ITownshipsPlugin;
import net.kingdomsofarden.townships.api.effects.Effect;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.util.StoredDataSection;

public class EffectProtection implements Effect {

    private Region region;

    @Override
    public String getName() {
        return "protection";
    }

    @Override
    public void onInit(ITownshipsPlugin plugin) {
    }

    @Override
    public void onLoad(ITownshipsPlugin plugin, Region region, StoredDataSection data) {
        this.region = region;
    }

    @Override
    public void onUnload(ITownshipsPlugin plugin, Region region, StoredDataSection data) {
        this.region = null;
        // Do nothing, this is a core effect and listeners will handle logic
    }

    @Override
    public Region getRegion() {
        return region;
    }

}
