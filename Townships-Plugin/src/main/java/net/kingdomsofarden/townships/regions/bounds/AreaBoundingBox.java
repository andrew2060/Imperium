package net.kingdomsofarden.townships.regions.bounds;

import net.kingdomsofarden.townships.api.regions.bounds.BoundingBox;
import net.kingdomsofarden.townships.api.regions.bounds.CuboidBoundingBox;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Collection;

public class AreaBoundingBox implements CuboidBoundingBox {

    private final int minX;
    private final int maxX;
    private final int minZ;
    private final int maxZ;
    private final int minY;
    private final int maxY;
    private World world;
    private Collection<Integer[]> vertices;

    public AreaBoundingBox(World world, int minX, int maxX, int minZ, int maxZ) {
        this.minX = minX;
        this.maxX = maxX;
        this.minZ = minZ;
        this.maxZ = maxZ;
        this.minY = Integer.MIN_VALUE;
        this.maxY = Integer.MAX_VALUE;
        this.world = world;
        this.vertices = new ArrayList<Integer[]>(8);
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
        return loc.getWorld().equals(world) && isInBounds(loc.getX(), loc.getY(), loc.getZ());
    }

    @Override
    public boolean isInBounds(double x, double y, double z) {
        return (minX <= x) && (x <= maxX) && (minY <= y) && (y <= maxY) && (minZ <= z) && (z <= maxZ);
    }

    @Override
    public boolean intersects(BoundingBox box) {
        if (!box.getWorld().equals(this.world)) {
            return false;
        }
        for (Integer[] vertex : box.getVertices()) {
            if (isInBounds(vertex[0], vertex[1], vertex[2])) {
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
        return world;
    }

    @Override
    public boolean encapsulates(BoundingBox other) {
        for (Integer[] vertex : other.getVertices()) {
            if (!isInBounds(vertex[0], vertex[1], vertex[2])) {
                return false;
            }
        }
        return true;
    }
}
