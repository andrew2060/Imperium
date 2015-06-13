package net.kingdomsofarden.townships.regions.bounds;

import net.kingdomsofarden.townships.api.math.Point3I;
import net.kingdomsofarden.townships.api.regions.bounds.BoundingArea;
import org.bukkit.World;

public class AreaBoundingBox extends AbstractCuboidBoundingBox {
    public AreaBoundingBox(World world, int minX, int maxX, int minZ, int maxZ) {
        super(world, new Point3I(minX, Integer.MIN_VALUE, minZ),
            new Point3I(maxX, Integer.MAX_VALUE, maxZ));
    }

    @Override protected <T extends BoundingArea> T produceGrown(int size) {
        return (T) new AreaBoundingBox(world, minX - size, maxX + size, minZ - size, maxZ + size);
    }

    @Override protected BoundingArea generateFlattened() {
        return this; // We can just return this since this covers all possible y values anyways
    }
}
