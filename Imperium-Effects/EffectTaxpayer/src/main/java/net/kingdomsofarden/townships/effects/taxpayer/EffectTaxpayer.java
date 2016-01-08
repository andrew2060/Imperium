package net.kingdomsofarden.townships.effects.taxpayer;

import net.kingdomsofarden.townships.api.ITownshipsPlugin;
import net.kingdomsofarden.townships.api.effects.Effect;
import net.kingdomsofarden.townships.api.regions.FunctionalRegion;
import net.kingdomsofarden.townships.api.resources.EconomyProvider;
import net.kingdomsofarden.townships.api.util.StoredDataSection;
import net.kingdomsofarden.townships.resources.VaultEconomyProvider;

public class EffectTaxpayer implements Effect {
    private FunctionalRegion region;

    @Override public String getName() {
        return "taxpayer";
    }

    @Override public void onInit(ITownshipsPlugin plugin) {
    }

    @Override public void onLoad(ITownshipsPlugin plugin, FunctionalRegion region, StoredDataSection data) {
        region.getEconomyProviders().put(EconomyProvider.TREASURY,
            new VaultEconomyProvider(region.getUid(), region, EconomyProvider.TREASURY));
        this.region = region;
    }

    @Override public void onUnload(ITownshipsPlugin plugin, FunctionalRegion region, StoredDataSection data) {
        // Nothing to save
    }

    @Override public FunctionalRegion getRegion() {
        return region;
    }
}
