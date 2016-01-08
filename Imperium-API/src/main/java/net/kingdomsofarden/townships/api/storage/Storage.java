package net.kingdomsofarden.townships.api.storage;


import net.kingdomsofarden.townships.api.regions.FunctionalRegion;

import java.util.Collection;
import java.util.UUID;

public interface Storage {

    FunctionalRegion loadRegion(UUID id);

    void saveRegion(FunctionalRegion r, boolean async);

    void removeRegion(UUID id);

    /**
     * @param regions Populates the given region collection with all regions in the given storage
     */
    void loadAllRegions(Collection<FunctionalRegion> regions);
}
