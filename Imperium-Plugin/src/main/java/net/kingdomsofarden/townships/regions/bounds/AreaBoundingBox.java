package net.kingdomsofarden.townships.regions.bounds;

import com.google.gson.JsonObject;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import net.kingdomsofarden.townships.api.regions.FunctionalRegion;
import net.kingdomsofarden.townships.api.regions.bounds.BoundingArea;
import net.kingdomsofarden.townships.api.regions.bounds.CuboidBoundingBox;
import net.kingdomsofarden.townships.regions.bounds.wrappers.WrappedBoundingArea;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.awt.*;
import java.awt.geom.Area;
import java.util.Map;

public class AreaBoundingBox extends WrappedBoundingArea implements CuboidBoundingBox {

    protected int maxX;
    protected int maxY;
    protected int maxZ;
    protected int minX;
    protected int minY;
    protected int minZ;
    protected World world;

    public AreaBoundingBox(World world, int minX, int maxX, int minZ, int maxZ) {
        super(new CuboidRegion(((com.sk89q.worldedit.world.World) new BukkitWorld(world)),
            new Vector(minX, 0, minZ), new Vector(maxX, 255, maxZ)), null);
        this.minX = minX;
        this.maxX = maxX;
        this.minZ = minZ;
        this.maxZ = maxZ;
    }

    @Override public boolean isInBounds(Location loc) {
        return loc.getWorld().getUID().equals(world.getUID()) && isInBounds(loc.getX(), loc.getY(),
            loc.getZ());
    }

    @Override public boolean isInBounds(double x, double y, double z) {
        return (minX <= x) && (x <= maxX) && (minY <= y) && (y <= maxY) && (minZ <= z) && (z
            <= maxZ);
    }

    @Override public Map<Material, Integer> checkForBlocks(Map<Material, Integer> blocks) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override public int area() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override public int volume() {
        throw new UnsupportedOperationException("Not implemented");
    }


    @Override public <T extends BoundingArea> T grow(Class<T> clazz, int size) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override public FunctionalRegion getRegion() {
        return null; // Unused
    }

    @Override public JsonObject toJson() {
        return null; // Unused
    }

    @Override public Region getBacking() {
        return null; // Unused
    }

    @Override public Area asAWTArea() {
        return new Area(
            new Rectangle(getMinX(), getMinZ(), getMaxX() - getMinX(), getMaxZ() - getMinZ()));
    }

    @Override public int getMinX() {
        return minX;
    }

    @Override public int getMaxX() {
        return maxX;
    }

    @Override public int getMinY() {
        return minY;
    }

    @Override public int getMaxY() {
        return maxY;
    }

    @Override public int getMinZ() {
        return minZ;
    }

    @Override public int getMaxZ() {
        return maxZ;
    }

    @Override public CuboidRegion getBackingBounds() {
        return bounds;
    }
}
