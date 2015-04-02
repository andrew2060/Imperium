package net.kingdomsofarden.townships.api.util;

import org.bukkit.Location;

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

    // Bounding Methods TODO Javadocs
    int getMinX();
    int getMaxX();
    int getMinY();
    int getMaxY();
    int getMinZ();
    int getMaxZ();

}
