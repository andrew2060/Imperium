package net.kingdomsofarden.townships.api.regions;

import com.google.common.base.Optional;
import net.kingdomsofarden.townships.api.regions.bounds.BoundingBox;
import org.bukkit.Location;

import java.util.Collection;


public interface RegionManager extends Collection<Region> {
    /**
     * @param loc The location to check
     * @return A Collection of Regions that bound (contain) the given location
     */
    Collection<Region> getBoundingRegions(Location loc);

    /**
     * @param loc The location to check
     * @return The {@link Area} bounding a given location, or absent if no such Area exists (no Regions exist in that Area)
     */
    Optional<Area> getBoundingArea(Location loc);

    /**
     * @param bounds The bounds to check
     * @return A collection of all regions that intersect this bounding box in some way
     */
    Collection<Region> getIntersectingRegions(BoundingBox bounds);
}
