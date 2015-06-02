package net.kingdomsofarden.townships.regions.bounds;

import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.regions.bounds.BoundingArea;
import net.kingdomsofarden.townships.api.regions.bounds.CuboidBoundingBox;
import net.kingdomsofarden.townships.api.regions.bounds.RegionBoundingArea;
import org.bukkit.Location;

public class RegionAxisAlignedBoundingBox extends AxisAlignedBoundingBox
    implements RegionBoundingArea, CuboidBoundingBox {
    private final Region region;

    public RegionAxisAlignedBoundingBox(Region r, Location loc1, Location loc2) {
        super(loc1, loc2);
        this.region = r;
    }

    @Override public Region getRegion() {
        return region;
    }

    @Override public <T extends BoundingArea> T grow(int size) {
        return (T) new RegionAxisAlignedBoundingBox(region,
            new Location(world, minX, minY, minZ).subtract(size, size, size),
            new Location(world, maxX, maxY, maxZ).add(size, size, size));
    }
}
