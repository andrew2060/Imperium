package net.kingdomsofarden.townships.util;

import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.util.BoundingBox;
import org.bukkit.Location;

import java.util.UUID;

public class AxisAlignedBoundingBox implements BoundingBox {
    private final Region region;
    private final UUID world;
    private int maxX;
    private int maxY;
    private int maxZ;
    private int minX;
    private int minY;
    private int minZ;

    public AxisAlignedBoundingBox(Region r, int lenX, int lenY, int lenZ) {
        Location center = r.getLocation();
        int x = center.getBlockX();
        int y = center.getBlockY();
        int z = center.getBlockZ();
        this.maxX = x + lenX;
        this.minX = x - lenX;
        this.maxY = y + lenY;
        this.minY = y - lenY;
        this.maxZ = z + lenZ;
        this.minZ = z - lenZ;
        this.region = r;
        this.world = center.getWorld().getUID();
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
