package net.kingdomsofarden.townships.api.regions.bounds;

import net.kingdomsofarden.townships.api.math.Geometry;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.Map;

public interface BoundingArea {

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
    boolean intersects(BoundingArea box);

    /**
     * @return The geometry associated with this bounding area (post intersections being processed
     * out)
     */
    Geometry getBoundGeometry();

    /**
     * @return The world containing this bounding box
     */
    World getWorld();

    /**
     * @param other The other bounding box to check
     * @return True if this bounding box completely encapsulates the other
     */
    boolean encapsulates(BoundingArea other);

    /**
     * Checks the given region for the parameter blocks
     *
     * @param blocks A mapping of block type/amount to check for
     * @return A mapping of remainder block type/amount not found
     */
    Map<Material, Integer> checkForBlocks(Map<Material, Integer> blocks);

    /**
     * @return The 2-dimensional area of this bounding box
     */
    int size2d();

    /**
     * @return The block volume of this bounding box
     */
    int volume();

    /**
     * @param clazz The class of the resulting bounding area to obtain
     * @param size The size to grow by
     * @return A clone of this bounding area that is grown by the specified dimensions
     * @throws IllegalArgumentException if clazz is not a bounding area type that can be derived
     * from this bounding area
     */
    <T extends BoundingArea> T grow(Class<T> clazz, int size);


    /**
     * @return A flattened (2D) representation of this bounding area, at y=0
     */
    BoundingArea flatten();

}
