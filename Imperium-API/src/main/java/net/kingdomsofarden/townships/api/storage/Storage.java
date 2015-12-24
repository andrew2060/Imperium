package net.kingdomsofarden.townships.api.storage;


import net.kingdomsofarden.townships.api.regions.Region;

import java.util.Collection;
import java.util.UUID;

public interface Storage {

    Region loadRegion(UUID id);

    void saveRegion(Region r, boolean async);

    void removeRegion(UUID id);

    /**
     * @param regions Populates the given region collection with all regions in the given storage
     */
    void loadAllRegions(Collection<Region> regions);
}
