package net.kingdomsofarden.townships.regions.bounds;

import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.regions.bounds.BoundingArea;
import net.kingdomsofarden.townships.api.regions.bounds.RegionBoundingArea;
import org.bukkit.Location;

public class RegionAxisAlignedBoundingBox extends AxisAlignedBoundingBox
    implements RegionBoundingArea {
    private final Region region;

    public RegionAxisAlignedBoundingBox(Region r, Location loc1, Location loc2) {
        super(loc1, loc2);
        this.region = r;
    }

    @Override public Region getRegion() {
        return region;
    }

    @Override protected <T extends BoundingArea> T produceGrown(int size) {
        Location loc1 = new Location(world, minX, minY, minZ).subtract(size, size, size);
        Location loc2 = new Location(world, maxX, maxY, maxZ).add(size, size, size);
        return (T) new RegionAxisAlignedBoundingBox(region, loc1, loc2);
    }

    @Override protected BoundingArea generateFlattened() {
        return new RegionAxisAlignedBoundingBox(region, new Location(world, minX, 0, minZ),
            new Location(world, maxX, 0, maxZ));
    }

}
