package net.kingdomsofarden.townships.api.regions;

import com.google.common.base.Optional;
import net.kingdomsofarden.townships.api.regions.bounds.BoundingArea;
import net.kingdomsofarden.townships.api.regions.bounds.RegionBoundingArea;
import org.bukkit.Location;

import java.util.Collection;
import java.util.TreeSet;
import java.util.UUID;


public interface RegionManager extends Collection<Region> {
    /**
     * @param loc The location to check
     * @return A Collection of Regions that bound (contain) the given location
     */
    TreeSet<Region> getBoundingRegions(Location loc);

    /**
     * @param loc The location to check
     * @return The {@link Area} bounding a given location, or absent if no such Area exists (no Regions exist in that Area)
     */
    Optional<Area> getBoundingArea(Location loc);

    /**
     * @param bounds The bounds to check
     * @return A collection of all regions that intersect this bounding box in some way
     */
    TreeSet<Region> getIntersectingRegions(BoundingArea bounds);

    /**
     * @param bounds The bounds to check
     * @return A collection of bounding areas that intersect the given bounds
     */
    Collection<RegionBoundingArea> getIntersectingBounds(BoundingArea bounds);

    /**
     * @param name The name of the region, non case-sensitive
     * @return The matching region, if present
     */
    Optional<Region> get(String name);

    /**
     * @param uuid The uid of the region
     * @return The matching region, if present
     */
    Optional<Region> get(UUID uuid);

}
