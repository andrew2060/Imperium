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
import java.util.Collection;
import java.util.Map;

public class CuboidSelection implements CuboidBoundingBox {

    private Location loc1 = null, loc2 = null;

    public boolean isValid() {
        return loc1 != null && loc2 != null;
    }

    @Override public boolean isInBounds(Location loc) {
        if (!isValid()) {
            throw new IllegalStateException(
                "A call was made without the selection being complete!");
        }
        return loc.getWorld().equals(loc1.getWorld()) && isInBounds(loc.getX(), loc.getY(),
            loc.getZ());
    }

    @Override public boolean isInBounds(double x, double y, double z) {
        if (!isValid()) {
            throw new IllegalStateException(
                "A call was made without the selection being complete!");
        }
        return (getMinX() <= x) && (x <= getMaxX()) && (getMinY() <= y) && (y <= getMaxY()) && (
            getMinZ() <= z) && (z <= getMaxZ());
    }

    @Override public boolean intersects(BoundingArea box) {
        if (!box.getWorld().equals(getWorld())) {
            return false;
        }
        for (Point3I vertex : box.getBoundGeometry().getVertices()) {
            if (box.isInBounds(vertex.getX(), vertex.getY(), vertex.getZ())) {
                return true;
            }
        }
        return box.encapsulates(this);
    }

    @Override public Geometry getBoundGeometry() {
        return null;
    }

    @Override public RectangularGeometry getRawRectangularGeometry() {
        return null;
    }

    @Override public int getMinX() {
        if (!isValid()) {
            throw new IllegalStateException(
                "A call was made without the selection being complete!");
        }
        return Math.min(loc1.getBlockX(), loc2.getBlockX());
    }

    @Override public int getMaxX() {
        if (!isValid()) {
            throw new IllegalStateException(
                "A call was made without the selection being complete!");
        }
        return Math.max(loc1.getBlockX(), loc2.getBlockX());
    }

    @Override public int getMinY() {
        if (!isValid()) {
            throw new IllegalStateException(
                "A call was made without the selection being complete!");
        }
        return Math.min(loc1.getBlockY(), loc2.getBlockY());
    }

    @Override public int getMaxY() {
        if (!isValid()) {
            throw new IllegalStateException(
                "A call was made without the selection being complete!");
        }
        return Math.max(loc1.getBlockY(), loc2.getBlockY());
    }

    @Override public int getMinZ() {
        if (!isValid()) {
            throw new IllegalStateException(
                "A call was made without the selection being complete!");
        }
        return Math.min(loc1.getBlockZ(), loc2.getBlockZ());
    }

    @Override public int getMaxZ() {
        if (!isValid()) {
            throw new IllegalStateException(
                "A call was made without the selection being complete!");
        }
        return Math.max(loc1.getBlockZ(), loc2.getBlockZ());
    }

    public Location getLoc1() {
        return loc1;
    }

    public void setLoc1(Location loc1) {
        this.loc1 = loc1;
        if (loc2 != null && !loc1.getWorld().equals(loc2.getWorld())) {
            loc2 = null;
        }
    }

    public Location getLoc2() {
        return loc2;
    }

    public void setLoc2(Location loc2) {
        this.loc2 = loc2;
        if (loc1 != null && !loc2.getWorld().equals(loc1.getWorld())) {
            loc1 = null;
        }
    }

    public World getWorld() {
        if (!isValid()) {
            throw new IllegalStateException(
                "A call was made without the selection being complete!");
        }
        return loc1.getWorld();
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
        return null;
    }

    @Override public int size2d() {
        return 0;
    }

    @Override public int volume() {
        return 0;
    }

    @Override public <T extends BoundingArea> T grow(int size) {
        return null;
    }

    @Override public BoundingArea flatten() {
        return null;
    }
}
