package net.kingdomsofarden.townships.regions.collections;

import com.google.common.base.Optional;
import net.kingdomsofarden.townships.api.regions.Area;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.util.BoundingBox;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * A terminal (i.e. no longer divisive) subset of the bound collection
 */
public class TerminalBoundCollection extends RegionBoundCollection {

    private Set<Region> contents; // TODO: use heap/binary tree instead for most efficient searching for a specific region?

    public TerminalBoundCollection(int xLeft, int xRight, int zLower, int zUpper) {
        this.contents = new LinkedHashSet<Region>();
        this.minX = xLeft;
        this.maxX = xRight;
        this.minZ = zLower;
        this.maxZ = zUpper;
    }

    @Override
    public Collection<Region> getBoundingRegions(int x, int y, int z) {
        Collection<Region> ret = new LinkedList<Region>();
        for (Region r : contents) {
            if (r.getBounds().isInBounds(x, y, z)) {
                ret.add(r);
            }
        }
        return ret;
    }

    // Inherited Stuff

    @Override
    public boolean add(BoundingBox bound) {
        return contents.add(bound.getRegion());
    }

    @Override
    public int size() {
        return contents.size();
    }

    @Override
    public boolean isEmpty() {
        return contents.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return contents.contains(o);
    }

    @Override
    public Iterator<Region> iterator() {
        return contents.iterator();
    }

    @Override
    public Object[] toArray() {
        return contents.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return contents.toArray(a);
    }

    @Override
    public boolean remove(Object o) {
        return contents.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return contents.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Region> c) {
        return contents.addAll(c);
    }

    @Override
    protected Area getQuadrant(int quad) {
        throw new UnsupportedOperationException("Terminal Areas do not have quadrants!");
    }

    @Override
    protected void constructContainedRegions(Set<Region> regions) {
        regions.addAll(contents);
    }

    @Override
    public Optional<Area> getBoundingArea(int x, int z) {
        return Optional.of((Area)this); // Not sure why this cast is needed but compiler complains
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return contents.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return contents.retainAll(c);
    }

    @Override
    public void clear() {
        contents.clear();
    }
}
