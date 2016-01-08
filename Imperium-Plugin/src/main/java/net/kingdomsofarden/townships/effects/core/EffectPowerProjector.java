package net.kingdomsofarden.townships.effects.core;

import net.kingdomsofarden.townships.api.ITownshipsPlugin;
import net.kingdomsofarden.townships.api.effects.Effect;
import net.kingdomsofarden.townships.api.regions.FunctionalRegion;
import net.kingdomsofarden.townships.api.util.StoredDataSection;

public class EffectPowerProjector implements Effect {

    public static final String NAME = "power_projector";

    @Override public String getName() {
        return NAME;
    }

    @Override public void onInit(ITownshipsPlugin plugin) {

    }

    @Override public void onLoad(ITownshipsPlugin plugin, FunctionalRegion region, StoredDataSection data) {

    }

    @Override public void onUnload(ITownshipsPlugin plugin, FunctionalRegion region, StoredDataSection data) {

    }

    @Override public FunctionalRegion getRegion() {
        return null;
    }
}
