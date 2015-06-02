package net.kingdomsofarden.townships.api;

import net.kingdomsofarden.townships.api.characters.CitizenManager;
import net.kingdomsofarden.townships.api.configuration.Configuration;
import net.kingdomsofarden.townships.api.effects.EffectManager;
import net.kingdomsofarden.townships.api.regions.RegionManager;
import net.kingdomsofarden.townships.api.storage.Storage;

public interface ITownshipsPlugin {
    RegionManager getRegions();

    CitizenManager getCitizens();

    EffectManager getEffectManager();

    Configuration getConfiguration();

    Storage getStorage();

    <T extends ITownshipsPlugin> T getBackingImplementation();
}
