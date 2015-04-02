package net.kingdomsofarden.townships.command.selection;

import net.kingdomsofarden.townships.api.util.BoundingBox;
import org.bukkit.Location;
import org.bukkit.World;

public class Selection implements BoundingBox {

    private Location loc1, loc2;

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
}
