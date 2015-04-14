package net.kingdomsofarden.townships.regions.bounds;

import net.kingdomsofarden.townships.api.regions.bounds.BoundingBox;
import net.kingdomsofarden.townships.api.regions.bounds.CuboidBoundingBox;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Collection;

public class AxisAlignedBoundingBox implements CuboidBoundingBox {
    private ArrayList<Integer[]> vertices;
    protected int maxX;
    protected int maxY;
    protected int maxZ;
    protected int minX;
    protected int minY;
    protected int minZ;
    protected World world;

    public AxisAlignedBoundingBox(Location loc1, Location loc2) {
        if (!loc1.getWorld().equals(loc2.getWorld())) {
            throw new IllegalStateException("Mismatched world locations!");
        }
        minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
        minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
        world = loc1.getWorld();
        vertices = new ArrayList<Integer[]>(8);
        vertices.add(new Integer[] {minX, minY, minZ});
        vertices.add(new Integer[] {minX, maxY, minZ});
        vertices.add(new Integer[] {minX, minY, maxZ});
        vertices.add(new Integer[] {minX, maxY, maxZ});
        vertices.add(new Integer[] {maxX, minY, minZ});
        vertices.add(new Integer[] {maxX, maxY, minZ});
        vertices.add(new Integer[] {maxX, minY, maxZ});
        vertices.add(new Integer[] {maxX, maxY, maxZ});
    }

    @Override
    public boolean isInBounds(Location loc) {
        return loc.getWorld().equals(world) && isInBounds(loc.getX(), loc.getY(), loc.getZ());
    }

    @Override
    public boolean isInBounds(double x, double y, double z) {
        return (minX <= x) && (x <= maxX) && (minY <= y) && (y <= maxY) && (minZ <= z) && (z <= maxZ);
    }

    @Override
    public boolean intersects(BoundingBox box, boolean recurs) {
        if (!box.getWorld().equals(world)) {
            return false;
        }
        for (Integer[] vertex : box.getVertices()) {
            if (box.isInBounds(vertex[0], vertex[1], vertex[2])) {
                return true;
            }
        }
        if (recurs) {
            return box.intersects(box, false);
        }
        return false;
    }

    @Override
    public Collection<Integer[]> getVertices() {
        return vertices;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public boolean encompasses(BoundingBox other) {
        for (Integer[] vertex : other.getVertices()) {
            if (!isInBounds(vertex[0], vertex[1], vertex[2])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int getMinX() {
        return minX;
    }

    @Override
    public int getMaxX() {
        return maxX;
    }

    @Override
    public int getMinY() {
        return minY;
    }

    @Override
    public int getMaxY() {
        return maxY;
    }

    @Override
    public int getMinZ() {
        return minZ;
    }

    @Override
    public int getMaxZ() {
        return maxZ;
    }
}
