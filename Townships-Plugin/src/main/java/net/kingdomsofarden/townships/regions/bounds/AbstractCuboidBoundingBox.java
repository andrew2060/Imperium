package net.kingdomsofarden.townships.regions.bounds;

import net.kingdomsofarden.townships.api.math.Geometry;
import net.kingdomsofarden.townships.api.math.Point3I;
import net.kingdomsofarden.townships.api.math.RectangularGeometry;
import net.kingdomsofarden.townships.api.regions.bounds.BoundingArea;
import net.kingdomsofarden.townships.api.regions.bounds.CuboidBoundingBox;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Map;

public abstract class AbstractCuboidBoundingBox implements CuboidBoundingBox {
    
    protected int maxX;
    protected int maxY;
    protected int maxZ;
    protected int minX;
    protected int minY;
    protected int minZ;
    protected World world;
    private ArrayList<Point3I> vertices;
    
    public AbstractCuboidBoundingBox(World boundsWorld, Point3I point1, Point3I point2) {
        minX = Math.min(point1.getX(), point2.getX());
        maxX = Math.max(point1.getX(), point2.getX());
        minY = Math.min(point1.getY(), point2.getY());
        maxY = Math.max(point1.getY(), point2.getY());
        minZ = Math.min(point1.getZ(), point2.getZ());
        maxZ = Math.max(point1.getZ(), point2.getZ());
        world = boundsWorld;
        vertices = new ArrayList<Point3I>(8);
        vertices.add(new Point3I(minX, minY, minZ));
        vertices.add(new Point3I(minX, maxY, minZ));
        vertices.add(new Point3I(minX, minY, maxZ));
        vertices.add(new Point3I(minX, maxY, maxZ));
        vertices.add(new Point3I(maxX, minY, minZ));
        vertices.add(new Point3I(maxX, maxY, minZ));
        vertices.add(new Point3I(maxX, minY, maxZ));
        vertices.add(new Point3I(maxX, maxY, maxZ));
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

    @Override public boolean isInBounds(Location loc) {
        return loc.getWorld().equals(world) && isInBounds(loc.getX(), loc.getY(), loc.getZ());
    }

    @Override public boolean isInBounds(double x, double y, double z) {
        return (minX <= x) && (x <= maxX) && (minY <= y) && (y <= maxY) && (minZ <= z) && (z
            <= maxZ);
    }

    @Override public boolean intersects(BoundingArea bounds) {
        if (!bounds.getWorld().equals(this.world)) {
            return false;
        }
        for (Point3I vertex : bounds.getBoundGeometry().getVertices()) {
            if (isInBounds(vertex.getX(), vertex.getY(), vertex.getZ())) {
                return true;
            }
        }
        return bounds.encapsulates(this);
    }

    @Override public Geometry getBoundGeometry() {
        return null;
    }

    @Override public RectangularGeometry getRawRectangularGeometry() {
        return null;
    }

    @Override public World getWorld() {
        return world;
    }

    @Override public boolean encapsulates(BoundingArea other) {
        for (Point3I vertex : other.getBoundGeometry().getVertices()) {
            if (!isInBounds(vertex.getX(), vertex.getY(), vertex.getZ())) {
                return false;
            }
        }
        return true;
    }

    @Override public Map<Material, Integer> checkForBlocks(Map<Material, Integer> blocks) {
        return null; //TODO
    }

    @Override public int size2d() {
        return (maxZ - minZ) * (maxX - minX);
    }

    @Override public int volume() {
        return (maxZ - minZ) * (maxX - minX) * (maxY - minY);
    }

    @Override public <T extends BoundingArea> T grow(int size) {
        return null;
    }

    @Override public BoundingArea flatten() {
        return null;
    }

    private class CuboidGeometry {
        
    }

}
