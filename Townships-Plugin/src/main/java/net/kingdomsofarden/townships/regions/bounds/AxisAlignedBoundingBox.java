package net.kingdomsofarden.townships.regions.bounds;

import net.kingdomsofarden.townships.api.math.Point3I;
import net.kingdomsofarden.townships.api.regions.bounds.BoundingArea;
import org.bukkit.Location;

public class AxisAlignedBoundingBox extends AbstractCuboidBoundingBox {

    public AxisAlignedBoundingBox(Location loc1, Location loc2) {
        super(loc1.getWorld(), new Point3I(loc1.getBlockX(), loc1.getBlockY(), loc1.getBlockZ()),
            new Point3I(loc2.getBlockX(), loc2.getBlockY(), loc2.getBlockZ()));
        if (!loc1.getWorld().equals(loc2.getWorld())) {
            throw new IllegalStateException("Mismatched world locations!");
        }
    }

    @Override protected <T extends BoundingArea> T produceGrown(int size) {
        Location loc1 = new Location(world, minX, minY, minZ).subtract(size, size, size);
        Location loc2 = new Location(world, maxX, maxY, maxZ).add(size, size, size);
        return (T) new AxisAlignedBoundingBox(loc1, loc2);
    }

    @Override protected BoundingArea generateFlattened() {
        return new AxisAlignedBoundingBox(new Location(world, minX, 0, minZ), new Location(world,
         maxX, 0, maxZ));
    }
}
