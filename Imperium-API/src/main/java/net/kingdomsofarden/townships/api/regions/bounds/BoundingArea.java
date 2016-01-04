package net.kingdomsofarden.townships.api.regions.bounds;

import com.google.gson.JsonObject;
import net.kingdomsofarden.townships.api.regions.Region;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.awt.geom.Area;
import java.util.Map;

public interface BoundingArea {

    /**
     * @param loc The location to check
     * @return Whether a given location is within the bounds of this bounding geometry
     */
    boolean isInBounds(Location loc);

    /**
     * <b>World-Agnostic: </b> for world aware check use {@link #isInBounds(org.bukkit.Location)}
     *
     * @param x The x coordinate to check
     * @param y The y coordinate to check
     * @param z The z coordinate to check
     * @return Whether a given location is within the bounds of this bounding geometry
     */
    boolean isInBounds(double x, double y, double z);

    /**
     * @param box The other bounding geometry to check
     * @return True if this bounding geometry interacts with the parameter bounding geometry at some point
     */
    boolean intersects(BoundingArea box);

    /**
     * @return The world containing this bounding geometry
     */
    World getWorld();

    /**
     * @param other The other bounding geometry to check
     * @return True if this bounding geometry completely encapsulates the other
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
     * @return The 2-dimensional area of this bounding geometry
     */
    int area();

    /**
     * @return The block volume of this bounding geometry
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
     * @return The {@link Region} represented by this bounding box
     */
    Region getRegion();

    /**
     * Initializes the bounds with the given settings as provided as a JSON object
     */
    void initialize(JsonObject json);

    /**
     * @return a Json object serialization of this bounding geometry
     */
    JsonObject save();

    /**
     * @return The backing WorldEdit region reprresenting this bounding area
     */
    com.sk89q.worldedit.regions.Region getBacking();

    /**
     * @return A AWT representation of this area
     */
    Area asAWTArea();

}
