package net.kingdomsofarden.townships.api.regions.bounds;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.Collection;
import java.util.Map;

public interface BoundingBox {

    /**
     * @param loc The location to check
     * @return Whether a given location is within the bounds of this bounding box
     */
    boolean isInBounds(Location loc);

    /**
     * <b>World-Agnostic: </b> for world aware check use {@link #isInBounds(org.bukkit.Location)}
     *
     * @param x The x coordinate to check
     * @param y The y coordinate to check
     * @param z The z coordinate to check
     * @return Whether a given location is within the bounds of this bounding box
     */
    boolean isInBounds(double x, double y, double z);

    /**
     * @param box The other bounding box to check
     * @return True if this bounding box interacts with the parameter bounding box at some point
     */
    boolean intersects(BoundingBox box);

    /**
     * @return A sorted collection of vertices using Double[] {x, y, z} representation
     * Specifically, the collection is sorted in ascending order, from -x/-y/-z to +x/+y/+z
     */
    Collection<Integer[]> getVertices();

    /**
     * @return The world containing this bounding box
     */
    World getWorld();

    /**
     * @param other The other bounding box to check
     * @return True if this bounding box completely encapsulates the other
     */
    boolean encapsulates(BoundingBox other);

    /**
     * Checks the given region for the parameter blocks
     * @param blocks A mapping of block type/amount to check for
     * @return A mapping of remainder block type/amount not found
     */
    Map<Material,Integer> checkForBlocks(Map<Material, Integer> blocks);
}
