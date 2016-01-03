package net.kingdomsofarden.townships.regions.collections;

import com.google.common.base.Optional;
import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.regions.Area;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.regions.bounds.BoundingArea;
import net.kingdomsofarden.townships.api.regions.bounds.CuboidBoundingBox;
import net.kingdomsofarden.townships.api.regions.bounds.RegionBoundingArea;
import net.kingdomsofarden.townships.regions.bounds.AreaBoundingBox;
import org.bukkit.World;

import java.util.*;

/**
 * Represents a quadrant based tree-like bounding box subdivision
 */
public class QuadrantBoundCollection extends RegionBoundCollection {

    private RegionBoundCollection[] subRegions;
    private int xDivisor;
    private int zDivisor;

    public QuadrantBoundCollection(World world, int quadrant, RegionBoundCollection parent,
        int xLeft, int xRight, int zLower, int zUpper) {
        this.parent = parent;
        this.quadrant = quadrant;
        this.subRegions = new RegionBoundCollection[4]; // [][] {{0 | 1} | {2 | 3}}
        this.minX = xLeft;
        this.maxX = xRight;
        this.minZ = zLower;
        this.maxZ = zUpper;
        this.xDivisor = Math.floorDiv((xRight + xLeft), 2);
        this.zDivisor = Math.floorDiv((zUpper + zLower), 2);
        this.bounds = new AreaBoundingBox(world, minX, maxX, minZ, maxZ);
        this.world = world;
    }


    @Override public CuboidBoundingBox getBoundingBox() {
        return bounds;
    }

    @Override public TreeSet<Region> getBoundingRegions(int x, int y, int z) {
        boolean upperHalf = z > zDivisor;
        boolean leftHalf = x <= xDivisor;
        if (leftHalf) {
            if (upperHalf) {
                return subRegions[0] != null ?
                    subRegions[0].getBoundingRegions(x, y, z) :
                    new TreeSet<Region>();
            } else {
                return subRegions[2] != null ?
                    subRegions[2].getBoundingRegions(x, y, z) :
                    new TreeSet<Region>();
            }
        } else {
            if (upperHalf) {
                return subRegions[1] != null ?
                    subRegions[1].getBoundingRegions(x, y, z) :
                    new TreeSet<Region>();
            } else {
                return subRegions[3] != null ?
                    subRegions[3].getBoundingRegions(x, y, z) :
                    new TreeSet<Region>();
            }
        }
    }

    @Override public Collection<Citizen> getCitizensInArea() {
        HashSet<Citizen> s = new HashSet<Citizen>();
        for (RegionBoundCollection col : subRegions) {
            for (Citizen c : col.getCitizensInArea()) {
                s.add(c);
            }
        }
        return s;
    }

    @Override public int getContentVolume() {
        int sum = 0;
        for (RegionBoundCollection sub : subRegions) {
            sum += sub.getContentVolume();
        }
        return sum;
    }

    @Override public int getContentSurfaceArea() {
        int sum = 0;
        for (RegionBoundCollection sub : subRegions) {
            sum += sub.getContentSurfaceArea();
        }
        return sum;
    }

    @Override public boolean add(BoundingArea b) {
        if (b instanceof CuboidBoundingBox) {
            CuboidBoundingBox bound = (CuboidBoundingBox) b;
            boolean upperLeft = bound.getMaxZ() > zDivisor && bound.getMinX() <= xDivisor;
            boolean upperRight = bound.getMaxZ() > zDivisor && bound.getMaxX() > xDivisor;
            boolean lowerLeft = bound.getMinZ() <= zDivisor && bound.getMinX() <= xDivisor;
            boolean lowerRight = bound.getMinZ() <= zDivisor && bound.getMaxX() > xDivisor;
            if (upperLeft) {
                checkIndexAndCreate(0);
                upperLeft = subRegions[0].add(b);
            }
            if (upperRight) {
                checkIndexAndCreate(1);
                upperRight = subRegions[1].add(b);
            }
            if (lowerLeft) {
                checkIndexAndCreate(2);
                lowerLeft = subRegions[2].add(b);
            }
            if (lowerRight) {
                checkIndexAndCreate(3);
                lowerRight = subRegions[3].add(b);
            }
            return (upperLeft || upperRight || lowerLeft || lowerRight);
        } else {
            return false; // TODO
        }
    }

    // TODO: Alternative algorithms?
    @Override protected void constructContainedRegions(Set<Region> regions) {
        for (RegionBoundCollection c : subRegions) {
            c.constructContainedRegions(regions);
        }
    }


    @Override public Optional<Area> getBoundingArea(int x, int z) {
        boolean upperHalf = z > zDivisor;
        boolean leftHalf = x <= xDivisor;
        if (leftHalf) {
            if (upperHalf) {
                return subRegions[0] != null ?
                    subRegions[0].getBoundingArea(x, z) :
                    Optional.<Area>absent();
            } else {
                return subRegions[2] != null ?
                    subRegions[2].getBoundingArea(x, z) :
                    Optional.<Area>absent();
            }
        } else {
            if (upperHalf) {
                return subRegions[1] != null ?
                    subRegions[1].getBoundingArea(x, z) :
                    Optional.<Area>absent();
            } else {
                return subRegions[3] != null ?
                    subRegions[3].getBoundingArea(x, z) :
                    Optional.<Area>absent();
            }
        }
    }

    @Override public void getIntersectingRegions(BoundingArea b, TreeSet<Region> col) {
        if (b instanceof CuboidBoundingBox) {
            CuboidBoundingBox bound = (CuboidBoundingBox) b;
            boolean upperLeft = bound.getMaxZ() > zDivisor && bound.getMinX() <= xDivisor;
            boolean upperRight = bound.getMaxZ() > zDivisor && bound.getMaxX() > xDivisor;
            boolean lowerLeft = bound.getMinZ() <= zDivisor && bound.getMinX() <= xDivisor;
            boolean lowerRight = bound.getMinZ() <= zDivisor && bound.getMaxX() > xDivisor;
            if (upperLeft) {
                if (subRegions[0] != null)
                    subRegions[0].getIntersectingRegions(b, col);
            }
            if (upperRight) {
                if (subRegions[1] != null)
                    subRegions[1].getIntersectingRegions(b, col);
            }
            if (lowerLeft) {
                if (subRegions[2] != null)
                    subRegions[2].getIntersectingRegions(b, col);
            }
            if (lowerRight) {
                if (subRegions[3] != null)
                    subRegions[3].getIntersectingRegions(b, col);
            }
        } else {
            return; // TODO, non cuboid
        }
    }

    @Override public Collection<RegionBoundingArea> getContainedBounds() {
        return null;
    }

    @Override
    public Collection<RegionBoundingArea> getIntersectingBounds(BoundingArea bounds) {
        return null;
    }

    private void checkIndexAndCreate(int i) {
        if (subRegions[i] == null) {
            int w;
            int h;
            int xLeft;
            int xRight;
            int zLower;
            int zUpper;
            switch (i) {
                case 0:
                    w = xDivisor - minX;
                    h = maxZ - zDivisor;
                    xLeft = minX;
                    xRight = xDivisor;
                    zLower = zDivisor;
                    zUpper = maxZ;
                    break;
                case 1:
                    w = maxX - xDivisor + 1;
                    h = maxZ - zDivisor + 1;
                    xLeft = xDivisor;
                    xRight = maxX;
                    zLower = zDivisor;
                    zUpper = maxZ;
                    break;
                case 2:
                    w = xDivisor - minX;
                    h = zDivisor - minZ;
                    xLeft = minX;
                    xRight = xDivisor;
                    zLower = minZ;
                    zUpper = zDivisor;
                    break;
                case 3:
                    w = maxX - xDivisor;
                    h = zDivisor - minZ;
                    xLeft = xDivisor;
                    xRight = maxX;
                    zLower = minZ;
                    zUpper = zDivisor;
                    break;
                default:
                    throw new ArrayIndexOutOfBoundsException(i);
            }
            if (w <= 100 || h <= 100) { //TODO nicer partitioning
                subRegions[i] = new TerminalBoundCollection(world, xLeft, xRight, zLower, zUpper);
            } else {
                subRegions[i] =
                    new QuadrantBoundCollection(world, i, this, xLeft, xRight, zLower, zUpper);
            }
        }
    }


    @Override public int size() {
        return getContents().size();
    }

    @Override public boolean isEmpty() {
        for (int i = 0; i < 4; i++) {
            if (subRegions[i] != null && !subRegions[i].isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override public boolean contains(Object o) {
        for (int i = 0; i < 4; i++) {
            if (subRegions[i] == null || !subRegions[i].contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override public Iterator<Region> iterator() {
        return getContents().iterator();
    }

    @Override public Object[] toArray() {
        return getContents().toArray();
    }

    @Override public <T> T[] toArray(T[] a) {
        return getContents().toArray(a);
    }

    @Override public boolean remove(Object o) {
        BoundingArea b;
        if (o instanceof Region) {
            b = ((Region) o).getBounds();
        } else if (o instanceof BoundingArea) {
            b = (BoundingArea) o;
        } else {
            return false;
        }
        if (b instanceof CuboidBoundingBox) {
            CuboidBoundingBox bound = (CuboidBoundingBox) b;
            boolean upperLeft = bound.getMaxZ() > zDivisor && bound.getMinX() <= xDivisor;
            boolean upperRight = bound.getMaxZ() > zDivisor && bound.getMaxX() > xDivisor;
            boolean lowerLeft = bound.getMinZ() <= zDivisor && bound.getMinX() <= xDivisor;
            boolean lowerRight = bound.getMinZ() <= zDivisor && bound.getMaxX() > xDivisor;
            if (upperLeft) {
                upperLeft = subRegions[0] != null && subRegions[0].remove(bound);
            }
            if (upperRight) {
                upperRight = subRegions[1] != null && subRegions[1].remove(bound);
            }
            if (lowerLeft) {
                lowerLeft = subRegions[2] != null && subRegions[2].remove(bound);
            }
            if (lowerRight) {
                lowerRight = subRegions[3] != null && subRegions[3].remove(bound);
            }
            return (upperLeft || upperRight || lowerLeft || lowerRight);
        } else {
            return false; //TODO
        }
    }

    @Override public boolean containsAll(Collection<?> c) {  // TODO: faster algorithm for this
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override public boolean removeAll(Collection<?> c) {
        boolean ret = false;
        for (Object o : c) {
            if (remove(o)) {
                ret = true;
            }
        }
        return ret;
    }

    @Override public boolean retainAll(Collection<?> c) {
        boolean ret = false;
        for (Region r : getContents()) {
            if (!c.contains(r) && remove(r)) {
                ret = true;
            }
        }
        return ret;
    }

    @Override public void clear() {
        for (int i = 0; i < 4; i++) {
            if (subRegions[i] != null) {
                subRegions[i].clear();
                subRegions[i] = null;
            }
        }
    }
}
