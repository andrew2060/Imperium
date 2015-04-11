package net.kingdomsofarden.townships.regions.bounds;

import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.regions.bounds.CuboidBoundingBox;
import net.kingdomsofarden.townships.api.regions.bounds.RegionBoundingBox;
import org.bukkit.Location;

public class RegionAxisAlignedBoundingBox extends AxisAlignedBoundingBox implements RegionBoundingBox, CuboidBoundingBox {
    private final Region region;

    public RegionAxisAlignedBoundingBox(Region r, Location loc1, Location loc2) {
        super(loc1, loc2);
        this.region = r;
    }

    @Override
    public Region getRegion() {
        return region;
    }
}
