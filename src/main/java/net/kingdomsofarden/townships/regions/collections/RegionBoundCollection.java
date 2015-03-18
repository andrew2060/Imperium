package net.kingdomsofarden.townships.regions.collections;

import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.util.BoundingBox;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class RegionBoundCollection implements Collection<Region> {

    // Bounds: TODO JAVADOCS
    protected int minX;
    protected int maxX;
    protected int minZ;
    protected int maxZ;

    /**
     * @param x The x coordinate to check
     * @param y The y coordinate to check
     * @param z The z coordinate to check
     * @return A collection of regions within this region bound collection that contain the parameter
     * x/y/z coordinates in their bounding box
     */
    public abstract Collection<Region> getBoundingRegions(int x, int y, int z);


    @Override
    public boolean add(Region region) {
        return add(region.getBounds());
    }

    public abstract boolean add(BoundingBox bound);

    @Override
    public boolean addAll(Collection<? extends Region> c) {
        boolean ret = false;
        // Check bounds
        for (Region r : c) {
            if (!isInBounds(r.getBounds())) {
                throw new IllegalArgumentException("A specified region " + r + " was not contained in some part this " +
                        "collection's bounds");
            }
            if (add(r.getBounds())) {
                ret = true;
            }
        }
        return ret;
    }

    /**
     * @param b A bounding box to check
     * @return True if some portion of b falls within the area managed by this collection
     */
    protected boolean isInBounds(BoundingBox b) {
        boolean xMinBound = checkBounds(b.getMinX(), minX, maxX);
        boolean zMinBound = checkBounds(b.getMinZ(), minZ, maxZ);
        boolean xMaxBound = checkBounds(b.getMaxX(), minX, maxX);
        boolean zMaxBound = checkBounds(b.getMaxZ(), minZ, maxZ);
        boolean encapsulatesX = b.getMinX() < minX && b.getMaxX() > maxX;
        boolean encapsulatesZ = b.getMinZ() < minZ && b.getMaxZ() > maxZ;

        return ((xMinBound || xMaxBound) && (zMinBound || zMaxBound)) // Standard case, one corners
                // Completely contained
                || (encapsulatesX && encapsulatesZ)
                // Partially contained
                || (((xMinBound || xMaxBound) && encapsulatesZ) || ((zMinBound || zMaxBound) && encapsulatesX)) ;
    }

    protected boolean checkBounds(int x, int l, int u) {
        return l <= x && x <= u;
    }

    /**
     * @return A Collection representation of all the regions stored (that have part of their bounding box within)
     * this bounded collection
     */
    public Collection<Region> getContainedRegions() {
        Set<Region> ret = new HashSet<Region>();
        constructContainedRegions(ret);
        return ret;
    }

    protected abstract void constructContainedRegions(Set<Region> regions);
}
