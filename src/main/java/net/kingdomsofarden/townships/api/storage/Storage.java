package net.kingdomsofarden.townships.api.storage;


import net.kingdomsofarden.townships.api.regions.Region;

import java.util.UUID;

public interface Storage {

    Region loadRegion(UUID id);

    void saveRegion(Region r, boolean async);
}
