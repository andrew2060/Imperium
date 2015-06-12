package net.kingdomsofarden.townships.regions.bounds;

import net.kingdomsofarden.townships.api.math.Point3I;
import org.bukkit.Location;

public class AxisAlignedBoundingBox extends AbstractCuboidBoundingBox {

    public AxisAlignedBoundingBox(Location loc1, Location loc2) {
        super(loc1.getWorld(), new Point3I(loc1.getBlockX(), loc1.getBlockY(), loc1.getBlockZ()),
            new Point3I(loc2.getBlockX(), loc2.getBlockY(), loc2.getBlockZ()));
        if (!loc1.getWorld().equals(loc2.getWorld())) {
            throw new IllegalStateException("Mismatched world locations!");
        }
    }

}
