package net.kingdomsofarden.townships.effects.incometax;

import net.kingdomsofarden.townships.api.ITownshipsPlugin;
import net.kingdomsofarden.townships.api.effects.Effect;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.util.StoredDataSection;

public class EffectIncomeTax implements Effect {
    private Region region;

    @Override
    public String getName() {
        return "income-tax";
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

    }

    @Override
    public Region getRegion() {
        return region;
    }
}
