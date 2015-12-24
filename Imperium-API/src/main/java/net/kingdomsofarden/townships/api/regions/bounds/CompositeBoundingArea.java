package net.kingdomsofarden.townships.api.regions.bounds;

import net.kingdomsofarden.townships.api.regions.Region;

import java.util.Collection;
import java.util.Map;

public interface CompositeBoundingArea extends BoundingArea, RegionBoundingArea {
    Map<String, Collection<Region>> getContents();
}
