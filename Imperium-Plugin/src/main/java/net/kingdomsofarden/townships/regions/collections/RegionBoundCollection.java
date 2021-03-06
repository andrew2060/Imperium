package net.kingdomsofarden.townships.regions.collections;

import com.google.common.base.Optional;
import net.kingdomsofarden.townships.api.regions.Area;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.regions.bounds.BoundingArea;
import net.kingdomsofarden.townships.api.regions.bounds.CuboidBoundingBox;
import net.kingdomsofarden.townships.api.regions.bounds.RegionBoundingArea;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public abstract class RegionBoundCollection implements Area {

    // Bounds:
    protected int minX;
    protected int maxX;
    protected int minZ;
    protected int maxZ;
    protected World world;
    protected CuboidBoundingBox bounds;


    // Inheritance
    protected RegionBoundCollection parent;
    protected int quadrant;



    @Override public int[] getBounds() {
        return new int[] {minX, maxX, minZ, maxZ};
    }

    @Override public CuboidBoundingBox getBoundingBox() {
        return bounds;
    }

    @Override public boolean add(Region region) {
        return add(region.getBounds());
    }

    public abstract boolean add(RegionBoundingArea bound);

    @Override public boolean addAll(Collection<? extends Region> c) {
        boolean ret = false;
        // Check bounds
        for (Region r : c) {
            if (!isInBounds(r.getBounds())) {
                throw new IllegalArgumentException(
                    "A specified region " + r + " was not contained in some part of this " +
                        "collection's bounds");
            }
            if (add(r.getBounds())) {
                ret = true;
            }
        }
        return ret;
    }

    public boolean isInBounds(Location loc) {
        return (checkBounds(loc.getX(), minX, maxX) && checkBounds(loc.getZ(), minZ, maxZ));
    }

    /**
     * @param b A bounding box to check
     * @return True if some portion of b falls within the area managed by this collection
     */
    protected boolean isInBounds(BoundingArea b) {
        return getBoundingBox().intersects(b);
    }

    protected boolean checkBounds(double x, int l, int u) {
        return l <= x && x <= u;
    }

    @Override public Collection<Region> getContents() {
        Set<Region> ret = new HashSet<Region>();
        constructContainedRegions(ret);
        return ret;
    }

    protected abstract void constructContainedRegions(Set<Region> regions);

    public abstract Optional<Area> getBoundingArea(int x, int z);

    public abstract void getIntersectingRegions(BoundingArea bounds, TreeSet<Region> col);

    public abstract Collection<RegionBoundingArea> getContainedBounds();

    public abstract Collection<RegionBoundingArea> getIntersectingBounds(BoundingArea bounds);
}
