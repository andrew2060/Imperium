package net.kingdomsofarden.townships.effects.taxpayer;

import net.kingdomsofarden.townships.api.ITownshipsPlugin;
import net.kingdomsofarden.townships.api.effects.Effect;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.resources.EconomyProvider;
import net.kingdomsofarden.townships.api.util.StoredDataSection;
import net.kingdomsofarden.townships.resources.VaultEconomyProvider;

public class EffectTaxpayer implements Effect {
    private Region region;

    @Override
    public String getName() {
        return "taxpayer";
    }

    @Override
    public void onInit(ITownshipsPlugin plugin) {}

    @Override
    public void onLoad(ITownshipsPlugin plugin, Region region, StoredDataSection data) {
        region.addEconomyProvider(new VaultEconomyProvider(region.getUid(), region, EconomyProvider.TREASURY));
        this.region = region;
    }

    @Override
    public void onUnload(ITownshipsPlugin plugin, Region region, StoredDataSection data) {
        // Nothing to save
    }

    @Override
    public Region getRegion() {
        return region;
    }
}
