package net.kingdomsofarden.townships.api.regions.bounds;

import net.kingdomsofarden.townships.api.regions.FunctionalRegion;

import java.util.Collection;
import java.util.Map;

public interface CompositeBoundingArea extends BoundingArea, RegionBoundingArea {
    Map<String, Collection<FunctionalRegion>> getContents();
}
