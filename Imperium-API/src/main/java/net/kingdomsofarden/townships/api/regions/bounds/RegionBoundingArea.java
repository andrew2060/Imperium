package net.kingdomsofarden.townships.api.regions.bounds;

import net.kingdomsofarden.townships.api.regions.Region;

public interface RegionBoundingArea extends BoundingArea {
    /**
     * @return The {@link Region} represented by this bounding box
     */
    Region getRegion();
}
