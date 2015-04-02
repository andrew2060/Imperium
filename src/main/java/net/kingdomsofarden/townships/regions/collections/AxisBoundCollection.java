package net.kingdomsofarden.townships.regions.collections;

import com.google.common.base.Optional;
import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.regions.Area;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.util.BoundingBox;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

public class AxisBoundCollection extends RegionBoundCollection {

    private static final int GRID_DIVISION = 10000;
    private static final int DEFAULT_CAPACITY = 5;

    enum AxisType {
        X,
        Z
    }

    private AxisType axis;
    private RegionBoundCollection[] positiveAxis;
    private RegionBoundCollection[] negativeAxis;


    public AxisBoundCollection(boolean init) {
        positiveAxis = new RegionBoundCollection[DEFAULT_CAPACITY];
        negativeAxis = new RegionBoundCollection[DEFAULT_CAPACITY];
        minX = minZ = Integer.MIN_VALUE;
        maxX = maxZ = Integer.MAX_VALUE;
        if (init)
            axis = AxisType.X;
        else
            axis = AxisType.Z;
    }

    public AxisBoundCollection(int xIdx) {
        this(false);
        if (xIdx >>> 31 == 1) {
            maxX = xIdx * GRID_DIVISION;
            minX = (xIdx - 1) * GRID_DIVISION;
        } else {
            minX = xIdx * GRID_DIVISION;
            maxX = (xIdx + 1) * GRID_DIVISION;
        }
    }

    @Override
    protected boolean add(BoundingBox bound) {
        int leftBound;
        int rightBound;
        if (axis == AxisType.X) {
            leftBound = bound.getMinX()/GRID_DIVISION;
            rightBound = bound.getMaxX()/GRID_DIVISION;
        } else {
            leftBound = bound.getMinZ()/GRID_DIVISION;
            rightBound = bound.getMaxZ()/GRID_DIVISION;
        }
        boolean crossSigns = leftBound >> 31 != rightBound >> 31;
        boolean ret = false;
        for (int i = leftBound; i != rightBound; i++) {
            switch (Integer.signum(i)) {
                case -1:
                    if (add(bound, i * -1, true)) {
                        ret = true;
                    }
                    break;
                case 0:
                    if (crossSigns) {
                        if (add(bound, 0, true)) {
                            ret = true;
                        }
                        if (add(bound, 0, false)) {
                            ret = true;
                        }
                    } else {
                        if (rightBound >>> 31 == 0) {
                            if (add(bound, 0, false)) {
                                ret = true;
                            }
                        } else {
                            if (add(bound, 0, true)) {
                                ret = true;
                            }
                        }
                    }
                    break;
                case 1:
                    if (add(bound, i, false)) {
                        ret = true;
                    }
                    break;
            }
        }
        return ret;
    }

    private boolean add(BoundingBox bound, int i, boolean negative) {
        RegionBoundCollection[] coll = ensureCapacity(i, negative);
        if (coll[i] == null) {
            if (axis == AxisType.X) {
                coll[i] = new AxisBoundCollection(negative ? -i : i);
            } else {
                int zMin;
                int zMax;
                if (negative) {
                    zMax = -i * GRID_DIVISION;
                    zMin = (-i - 1) * GRID_DIVISION;
                } else {
                    zMin = i * GRID_DIVISION;
                    zMax = (i + 1) * GRID_DIVISION;
                }
                coll[i] = new QuadrantBoundCollection(-1, this, minX, maxX, zMin, zMax);
            }
        }
        return coll[i].add(bound);
    }

    private RegionBoundCollection[] ensureCapacity(int i, boolean negative) {
        if (negative) {
            if (i >= negativeAxis.length) {
                negativeAxis = Arrays.copyOf(negativeAxis, i / DEFAULT_CAPACITY + DEFAULT_CAPACITY);
            }
            return negativeAxis;
        } else {
            if (i >= positiveAxis.length) {
                positiveAxis = Arrays.copyOf(positiveAxis, i / DEFAULT_CAPACITY + DEFAULT_CAPACITY);
            }
            return positiveAxis;
        }

    }

    @Override
    protected void constructContainedRegions(Set<Region> regions) {
        for (RegionBoundCollection col : positiveAxis) {
            col.constructContainedRegions(regions);
        }
        for (RegionBoundCollection col : negativeAxis) {
            col.constructContainedRegions(regions);
        }
    }

    private RegionBoundCollection get(int x, int z) {
        boolean neg = false;
        int idx;
        if (axis == AxisType.X) {
            idx = x/GRID_DIVISION;
        } else {
            idx = z/GRID_DIVISION;
        }
        if (idx >>> 31 == 1) {
            neg = true;
            idx *= -1;
        }
        RegionBoundCollection ret;
        if (neg) {
            ret = idx < negativeAxis.length ? negativeAxis[idx] : null;
        } else {
            ret = idx < positiveAxis.length ? positiveAxis[idx] : null;
        }
        return ret;
    }

    @Override
    public Optional<Area> getBoundingArea(int x, int z) {
        RegionBoundCollection ret = get(x, z);
        if (ret == null) {
            return Optional.absent();
        } else {
            return ret.getBoundingArea(x, z);
        }
    }

    @Override
    public BoundingBox getBoundingBox() {
        return null;
    }

    @Override
    public Collection<Region> getBoundingRegions(int x, int y, int z) {
        RegionBoundCollection ret = get(x, z);
        if (ret == null) {
            return new LinkedList<Region>();
        } else {
            return ret.getBoundingRegions(x, y, z);
        }
    }

    @Override
    public Collection<Citizen> getCitizensInArea() {
        HashSet s = new HashSet<Citizen>();
        for (RegionBoundCollection col : positiveAxis) {
            Collections.addAll(s, col.getCitizensInArea());
        }
        for (RegionBoundCollection col : negativeAxis) {
            Collections.addAll(s, col.getCitizensInArea());
        }
        return s;
    }

    @Override
    public int size() {
        int sum = 0;
        for (RegionBoundCollection col : positiveAxis) {
            sum += col.size();
        }
        for (RegionBoundCollection col : negativeAxis) {
            sum += col.size();
        }
        return sum;
    }

    @Override
    public boolean isEmpty() {
        for (RegionBoundCollection col : positiveAxis) {
            if (!(col.isEmpty())) {
                return false;
            }
        }
        for (RegionBoundCollection col : negativeAxis) {
            if (!(col.isEmpty())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean contains(Object o) {
        for (RegionBoundCollection col : positiveAxis) {
            if (col.contains(o)) {
                return true;
            }
        }
        for (RegionBoundCollection col : negativeAxis) {
            if (col.contains(o)) {
                return true;
            }
        }
        return false;
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
        int leftBound;
        int rightBound;
        if (axis == AxisType.X) {
            leftBound = bound.getMinX()/GRID_DIVISION;
            rightBound = bound.getMaxX()/GRID_DIVISION;
        } else {
            leftBound = bound.getMinZ()/GRID_DIVISION;
            rightBound = bound.getMaxZ()/GRID_DIVISION;
        }
        boolean crossSigns = leftBound >> 31 != rightBound >> 31;
        boolean ret = false;
        for (int i = leftBound; i != rightBound; i++) {
            switch (Integer.signum(i)) {
                case -1:
                    if (remove(bound, i * -1, true)) {
                        ret = true;
                    }
                    break;
                case 0:
                    if (crossSigns) {
                        if (remove(bound, 0, true)) {
                            ret = true;
                        }
                        if (remove(bound, 0, false)) {
                            ret = true;
                        }
                    } else {
                        if (rightBound >>> 31 == 0) {
                            if (remove(bound, 0, false)) {
                                ret = true;
                            }
                        } else {
                            if (remove(bound, 0, true)) {
                                ret = true;
                            }
                        }
                    }
                    break;
                case 1:
                    if (remove(bound, i, false)) {
                        ret = true;
                    }
                    break;
            }
        }
        return ret;
    }

    private boolean remove(BoundingBox bound, int i, boolean negative) {
        RegionBoundCollection[] coll = negative ? negativeAxis : positiveAxis;
        if (i >= coll.length || coll[i] == null) {
            return false;
        } else {
            return coll[i].remove(bound);
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
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
        for (int i = 0; i < positiveAxis.length; i++) {
            if (positiveAxis[i] != null) {
                positiveAxis[i].clear();
                positiveAxis[i] = null;
            }
        }
        for (int i = 0; i < negativeAxis.length; i++) {
            if (negativeAxis[i] != null) {
                negativeAxis[i].clear();
                negativeAxis[i] = null;
            }
        }
    }
}
