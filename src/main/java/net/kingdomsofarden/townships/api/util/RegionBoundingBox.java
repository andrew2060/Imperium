package net.kingdomsofarden.townships.api.util;

import net.kingdomsofarden.townships.api.regions.Region;

public interface RegionBoundingBox extends BoundingBox {
    /**
     * @return The {@link Region} represented by this bounding box
     */
    Region getRegion();
}
