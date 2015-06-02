package net.kingdomsofarden.townships.regions.bounds;

import net.kingdomsofarden.townships.api.regions.bounds.BoundingArea;
import net.kingdomsofarden.townships.api.regions.bounds.CuboidBoundingBox;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Collection;

public class CuboidSelection implements CuboidBoundingBox {

    private Location loc1 = null, loc2 = null;

    public boolean isValid() {
        return loc1 != null && loc2 != null;
    }

    @Override
    public boolean isInBounds(Location loc) {
        if (!isValid()) {
            throw new IllegalStateException("A call was made without the selection being complete!");
        }
        return loc.getWorld().equals(loc1.getWorld()) && isInBounds(loc.getX(), loc.getY(), loc.getZ());
    }

    @Override
    public boolean isInBounds(double x, double y, double z) {
        if (!isValid()) {
            throw new IllegalStateException("A call was made without the selection being complete!");
        }
        return (getMinX() <= x) && (x <= getMaxX()) && (getMinY() <= y)
                && (y <= getMaxY()) && (getMinZ() <= z) && (z <= getMaxZ());
    }

    @Override
    public boolean intersects(BoundingArea box) {
        if (!box.getWorld().equals(getWorld())) {
            return false;
        }
        for (Integer[] vertex : box.getVertices()) {
            if (box.isInBounds(vertex[0], vertex[1], vertex[2])) {
                return true;
            }
        }
        return box.encapsulates(this);
    }

    @Override
    public Collection<Integer[]> getVertices() {
        ArrayList<Integer[]> vertices = new ArrayList<Integer[]>(8);
        int minX = getMinX();
        int maxX = getMaxX();
        int minY = getMinY();
        int maxY = getMaxY();
        int minZ = getMinZ();
        int maxZ = getMaxZ();
        vertices.add(new Integer[] {minX, minY, minZ});
        vertices.add(new Integer[] {minX, maxY, minZ});
        vertices.add(new Integer[] {minX, minY, maxZ});
        vertices.add(new Integer[] {minX, maxY, maxZ});
        vertices.add(new Integer[] {maxX, minY, minZ});
        vertices.add(new Integer[] {maxX, maxY, minZ});
        vertices.add(new Integer[] {maxX, minY, maxZ});
        vertices.add(new Integer[] {maxX, maxY, maxZ});
        return vertices;
    }

    @Override
    public int getMinX() {
        if (!isValid()) {
            throw new IllegalStateException("A call was made without the selection being complete!");
        }
        return Math.min(loc1.getBlockX(), loc2.getBlockX());
    }

    @Override
    public int getMaxX() {
        if (!isValid()) {
            throw new IllegalStateException("A call was made without the selection being complete!");
        }
        return Math.max(loc1.getBlockX(), loc2.getBlockX());
    }

    @Override
    public int getMinY() {
        if (!isValid()) {
            throw new IllegalStateException("A call was made without the selection being complete!");
        }
        return Math.min(loc1.getBlockY(), loc2.getBlockY());
    }

    @Override
    public int getMaxY() {
        if (!isValid()) {
            throw new IllegalStateException("A call was made without the selection being complete!");
        }
        return Math.max(loc1.getBlockY(), loc2.getBlockY());
    }

    @Override
    public int getMinZ() {
        if (!isValid()) {
            throw new IllegalStateException("A call was made without the selection being complete!");
        }
        return Math.min(loc1.getBlockZ(), loc2.getBlockZ());
    }

    @Override
    public int getMaxZ() {
        if (!isValid()) {
            throw new IllegalStateException("A call was made without the selection being complete!");
        }
        return Math.max(loc1.getBlockZ(), loc2.getBlockZ());
    }

    public Location getLoc1() {
        return loc1;
    }

    public Location getLoc2() {
        return loc2;
    }

    public void setLoc1(Location loc1) {
        this.loc1 = loc1;
        if (loc2 != null && !loc1.getWorld().equals(loc2.getWorld())) {
            loc2 = null;
        }
    }

    public void setLoc2(Location loc2) {
        this.loc2 = loc2;
        if (loc1 != null && !loc2.getWorld().equals(loc1.getWorld())) {
            loc1 = null;
        }
    }

    public World getWorld() {
        if (!isValid()) {
            throw new IllegalStateException("A call was made without the selection being complete!");
        }
        return loc1.getWorld();
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
}
