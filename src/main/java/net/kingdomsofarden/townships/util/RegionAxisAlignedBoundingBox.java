package net.kingdomsofarden.townships.util;

import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.util.RegionBoundingBox;
import org.bukkit.Location;

public class RegionAxisAlignedBoundingBox extends AxisAlignedBoundingBox implements RegionBoundingBox {
    private final Region region;

    public RegionAxisAlignedBoundingBox(Region r, Location loc1, Location loc2) {
        super(loc1, loc2);
        this.region = r;
    }

    @Override
    public Region getRegion() {
        return region;
    }

    @Override
    public boolean isInBounds(Location loc) {
        return loc.getWorld().getUID().equals(world) && isInBounds(loc.getX(), loc.getY(), loc.getZ());
    }

    @Override
    public boolean isInBounds(double x, double y, double z) {
        return (minX <= x) && (x <= maxX) && (minY <= y) && (y <= maxY) && (minZ <= z) && (z <= maxZ);
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
