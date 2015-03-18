package net.kingdomsofarden.townships.api.regions;

import org.bukkit.Location;

import java.util.Collection;

public interface RegionManager extends Collection<Region> {
    /**
     * @param loc The location to check
     * @return A Collection of Regions that bound (contain) the given location
     */
    Collection<Region> getBoundingRegions(Location loc);
}
