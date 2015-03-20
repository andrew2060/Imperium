package net.kingdomsofarden.townships.regions.collections;

import com.google.common.base.Optional;
import net.kingdomsofarden.townships.api.regions.Area;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.util.BoundingBox;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

public class QuadrantBoundCollection extends RegionBoundCollection {

    private RegionBoundCollection[] subRegions;
    private int xDivisor;
    private int zDivisor;

    public QuadrantBoundCollection(int quadrant, RegionBoundCollection parent, int xLeft, int xRight, int zLower, int zUpper) {
        this.parent = parent;
        this.quadrant = quadrant;
        this.subRegions = new RegionBoundCollection[4]; // [][] {{0 | 1} | {2 | 3}}
        this.minX = xLeft;
        this.maxX = xRight;
        this.minZ = zLower;
        this.maxZ = zUpper;
        this.xDivisor = Math.floorDiv((xRight + xLeft), 2);
        this.zDivisor = Math.floorDiv((zUpper + zLower), 2);
    }


    @Override
    protected Area getQuadrant(int quad) {
        return subRegions[quad];
    }

    @Override
    public Collection<Region> getBoundingRegions(int x, int y, int z) {
        boolean upperHalf = z > zDivisor;
        boolean leftHalf = x <= xDivisor;
        if (leftHalf) {
            if (upperHalf) {
                return subRegions[0] != null ? subRegions[0].getBoundingRegions(x, y, z) : Collections.<Region>emptyList();
            } else {
                return subRegions[2] != null ? subRegions[2].getBoundingRegions(x, y, z) : Collections.<Region>emptyList();
            }
        } else {
            if (upperHalf) {
                return subRegions[1] != null ? subRegions[1].getBoundingRegions(x, y, z) : Collections.<Region>emptyList();
            } else {
                return subRegions[3] != null ? subRegions[3].getBoundingRegions(x, y, z) : Collections.<Region>emptyList();
            }
        }
    }

    @Override
    public boolean add(BoundingBox bound) {
        boolean upperLeft = bound.getMaxZ() > zDivisor && bound.getMinX() <= xDivisor;
        boolean upperRight = bound.getMaxZ() > zDivisor && bound.getMaxX() > xDivisor;
        boolean lowerLeft = bound.getMinZ() <= zDivisor && bound.getMinX() <= xDivisor;
        boolean lowerRight = bound.getMinZ() <= zDivisor && bound.getMaxX() > xDivisor;
        if (upperLeft) {
            checkIndexAndCreate(0);
            upperLeft = subRegions[0].add(bound);
        }
        if (upperRight) {
            checkIndexAndCreate(1);
            upperRight = subRegions[1].add(bound);
        }
        if (lowerLeft) {
            checkIndexAndCreate(2);
            lowerLeft = subRegions[2].add(bound);
        }
        if (lowerRight) {
            checkIndexAndCreate(3);
            lowerRight = subRegions[3].add(bound);
        }
        return (upperLeft || upperRight || lowerLeft || lowerRight);
    }


    // TODO: Alternative algorithms?
    @Override
    protected void constructContainedRegions(Set<Region> regions) {
        for (RegionBoundCollection c : subRegions) {
            regions.addAll(c.getContents());
        }
        return;
    }

    @Override
    public Optional<Area> getBoundingArea(int x, int z) {
        boolean upperHalf = z > zDivisor;
        boolean leftHalf = x <= xDivisor;
        if (leftHalf) {
            if (upperHalf) {
                return subRegions[0] != null ? subRegions[0].getBoundingArea(x, z) : Optional.<Area>absent();
            } else {
                return subRegions[2] != null ? subRegions[2].getBoundingArea(x, z) : Optional.<Area>absent();
            }
        } else {
            if (upperHalf) {
                return subRegions[1] != null ? subRegions[1].getBoundingArea(x, z) : Optional.<Area>absent();
            } else {
                return subRegions[3] != null ? subRegions[3].getBoundingArea(x, z) : Optional.<Area>absent();
            }
        }
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
                subRegions[i] = new TerminalBoundCollection(xLeft, xRight, zLower, zUpper);
            } else {
                subRegions[i] = new QuadrantBoundCollection(i, this, xLeft, xRight, zLower, zUpper);
            }
        }
    }


    @Override
    public int size() {
        return getContents().size();
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < 4; i++) {
            if (subRegions[i] != null && !subRegions[i].isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean contains(Object o) {
        for (int i = 0; i < 4; i++) {
            if (subRegions[i] == null || !subRegions[i].contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Iterator<Region> iterator() {
        return getContents().iterator();
    }

    @Override
    public Object[] toArray() {
        return getContents().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return getContents().toArray(a);
    }

    @Override
    public boolean remove(Object o) {
        BoundingBox bound;
        if (o instanceof Region) {
            bound = ((Region) o).getBounds();
        } else if (o instanceof BoundingBox) {
            bound = (BoundingBox) o;
        } else {
            return false;
        }
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
    }

    @Override
    public boolean containsAll(Collection<?> c) {  // TODO: faster algorithm for this
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean ret = false;
        for (Object o : c) {
            if (remove(o)) {
                ret = true;
            }
        }
        return ret;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean ret = false;
        for (Region r : getContents()) {
            if (!c.contains(r) && remove(r)) {
                ret = true;
            }
        }
        return ret;
    }

    @Override
    public void clear() {
        for (int i = 0; i < 4; i++) {
            if (subRegions[i] != null) {
                subRegions[i].clear();
                subRegions[i] = null;
            }
        }
    }
}
