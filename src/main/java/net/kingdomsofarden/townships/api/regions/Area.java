package net.kingdomsofarden.townships.api.regions;

import com.google.common.base.Optional;
import org.bukkit.Location;

import java.util.Collection;

/**
 * <p>Represents an Area of a given world with upper/lower x/z bounds. Depending on implementation,
 * Areas can contain other smaller Areas but can also be referred to as the collection of all regions that are within a
 * given area.</p>
 * <p><b>Warning: </b> The default implementation is relatively slow for any operations that may involve construction of a
 * collection of all contents with unique elements: the exception being if the Area involved is terminal (i.e. does not
 * contain other smaller Areas)</p>
 */
public interface Area extends Collection<Region> {

    /**
     * <b>Warning: </b> for the default implementation, this will be relatively slow for non-terminal Areas
     * @return A Collection representation of all the regions stored (that have part of their bounding box within)
     * this Area
     */
    Collection<Region> getContents();

    /**
     * @param direction The direction to get the neighbor for (0-7 clockwise from the left in 45 degree increments)
     * @return An Optional representation of the Area unit of the neighbor, will be null if one does not exist
     * (i.e. there is no Area that contains Regions within it directly neighboring this area). The guarantee is made
     * that the returned value (if it exists) will be of the same level as that of the originating Area: i.e. if the
     * request originated from a terminal area, the returned value would also be a terminal area
     */
    Optional<Area> getNeighbor(int direction);

    /**
     * @param x The x coordinate to check
     * @param y The y coordinate to check
     * @param z The z coordinate to check
     * @return A collection of regions within this region bound collection that contain the parameter
     * x/y/z coordinates in their bounding box
     */
    Collection<Region> getBoundingRegions(int x, int y, int z);

    /**
     * Utility method to check whether a given location is within the bounds tracked by this collection
     * @param loc The location to check
     * @return True if within the bounds represented by this collection
     */
    boolean isInBounds(Location loc);
}
