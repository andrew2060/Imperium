package net.kingdomsofarden.townships.api;

import net.kingdomsofarden.townships.api.characters.CitizenManager;
import net.kingdomsofarden.townships.api.regions.RegionManager;

public interface ITownshipsPlugin {
    RegionManager getRegions();
    CitizenManager getCitizens();
}
