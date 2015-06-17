package net.kingdomsofarden.townships.regions.bounds;

import net.kingdomsofarden.townships.api.regions.bounds.BoundingArea;
import org.bukkit.World;

import java.util.HashMap;

public class AreaBoundingBox extends AbstractCuboidBoundingBox {
    // A special exception is made for the requirement that constructors be empty
    // due to usage only internally, this way is faster
    public AreaBoundingBox(World world, int minX, int maxX, int minZ, int maxZ) {
        this.world = world;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = Integer.MIN_VALUE;
        this.maxY = Integer.MAX_VALUE;
        this.minZ = minZ;
        this.maxZ = maxZ;
        this.growths = new HashMap<>();
        this.geometry = new CuboidGeometry();
    }

    @SuppressWarnings("unchecked") @Override protected <T extends BoundingArea> T produceGrown(int size) {
        return (T) new AreaBoundingBox(world, minX - size, maxX + size, minZ - size, maxZ + size);
    }

    @Override protected BoundingArea generateFlattened() {
        return this; // We can just return this since this covers all possible y values anyways
    }
}
