package net.kingdomsofarden.townships.api.storage;


import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.util.StoredDataSection;

import java.util.UUID;

// TODO get rid of ConfigurationSection for something abstract
public interface Storage {
    StoredDataSection loadCitizen(UUID id);
    StoredDataSection loadRegionConfiguration(UUID id);

    boolean saveCitizen(Citizen c, boolean async);
    boolean saveRegion(Region r, boolean async);
}
