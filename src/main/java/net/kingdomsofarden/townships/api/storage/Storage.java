package net.kingdomsofarden.townships.api.storage;


import net.kingdomsofarden.townships.api.util.StoredDataSection;

import java.util.UUID;

// TODO get rid of ConfigurationSection for something abstract
public interface Storage {
    StoredDataSection loadCitizen(UUID id);
    StoredDataSection loadRegionConfiguration(UUID id);
}
