package net.kingdomsofarden.townships.regions.bounds;

import net.kingdomsofarden.townships.api.regions.bounds.BoundingArea;
import net.kingdomsofarden.townships.api.regions.bounds.CuboidBoundingBox;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class DynamicCuboidBoundingBox implements CuboidBoundingBox {
    private final int minX;
    private final int minY;
    private final int minZ;
    private final int maxX;
    private final int maxY;
    private final int maxZ;
    private ArrayList<Integer[]> vertices;


    public DynamicCuboidBoundingBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.vertices = new ArrayList<Integer[]>(8);
        this.vertices.add(new Integer[] {minX, minY, minZ});
        this.vertices.add(new Integer[] {minX, maxY, minZ});
        this.vertices.add(new Integer[] {minX, minY, maxZ});
        this.vertices.add(new Integer[] {minX, maxY, maxZ});
        this.vertices.add(new Integer[] {maxX, minY, minZ});
        this.vertices.add(new Integer[] {maxX, maxY, minZ});
        this.vertices.add(new Integer[] {maxX, minY, maxZ});
        this.vertices.add(new Integer[] {maxX, maxY, maxZ});
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

    @Override
    public boolean isInBounds(Location loc) {
        return isInBounds(loc.getX(), loc.getY(), loc.getZ());
    }

    @Override
    public boolean isInBounds(double x, double y, double z) {
        return (getMinX() <= x) && (x <= getMaxX()) && (getMinY() <= y)
                && (y <= getMaxY()) && (getMinZ() <= z) && (z <= getMaxZ());
    }

    @Override
    public boolean intersects(BoundingArea box) {
        for (Integer[] vertex : box.getVertices()) {
            if (box.isInBounds(vertex[0], vertex[1], vertex[2])) {
                return true;
            }
        }
        return box.encapsulates(this);
    }

    @Override
    public Collection<Integer[]> getVertices() {
        return vertices;
    }

    @Override
    public World getWorld() {
        throw new UnsupportedOperationException("Cannot get world in a dynamic bounding box");
    }

    @Override
    public boolean encapsulates(BoundingArea other) {
        for (Integer[] vertex : other.getVertices()) {
            if (!isInBounds(vertex[0], vertex[1], vertex[2])) {
                return false;
            }
        }
        return true; // TODO not correct
    }

    @Override
    public Map<Material, Integer> checkForBlocks(Map<Material, Integer> blocks) {
        return null;
    }

    @Override
    public int size2d() {
        return (maxX - minX) * (maxZ - minZ);
    }

    @Override
    public int volume() {
        return size2d() * (maxY - minY);
    }
}
