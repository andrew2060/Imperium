package net.kingdomsofarden.townships.regions.collections;

import com.google.common.base.Optional;
import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.regions.Area;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.regions.bounds.BoundingArea;
import net.kingdomsofarden.townships.api.regions.bounds.CuboidBoundingBox;
import net.kingdomsofarden.townships.api.regions.bounds.RegionBoundingArea;
import net.kingdomsofarden.townships.regions.TownshipsRegion;
import net.kingdomsofarden.townships.regions.bounds.AreaBoundingBox;
import org.bukkit.World;

import java.util.*;

/**
 * A terminal (i.e. no longer divisive) subset of the bound collection
 */
public class TerminalBoundCollection extends RegionBoundCollection {

    private Set<Region> contents;
    // TODO: use heap/binary tree instead for most efficient searching for a specific region?
    private Collection<Citizen> currCitizens;

    public TerminalBoundCollection(World world, int xLeft, int xRight, int zLower, int zUpper) {
        this.contents = new LinkedHashSet<Region>();
        this.minX = xLeft;
        this.maxX = xRight;
        this.minZ = zLower;
        this.maxZ = zUpper;
        this.currCitizens = new HashSet<Citizen>();
        this.world = world;
        this.bounds = new AreaBoundingBox(world, minX, maxX, minZ, maxZ);
    }

    @Override public CuboidBoundingBox getBoundingBox() {
        return bounds;
    }

    @Override public TreeSet<Region> getBoundingRegions(int x, int y, int z) {
        TreeSet<Region> ret = new TreeSet<Region>(new Comparator<Region>() {
            @Override public int compare(Region o1, Region o2) {
                int ret = o2.getTier() - o1.getTier();
                if (ret == 0) {
                    return o1.getUid().compareTo(o2.getUid());
                } else {
                    return ret;
                }
            }
        });
        for (Region r : contents) {
            if (r.getBounds().isInBounds(x, y, z)) {
                ret.add(r);
            }
        }
        return ret;
    }

    // Inherited Stuff

    @Override public boolean add(RegionBoundingArea bound) {
        TownshipsRegion r = (TownshipsRegion) bound.getRegion();
        r.getBoundingAreas().add(this);
        return contents.add(bound.getRegion());
    }

    @Override public int size() {
        return contents.size();
    }

    @Override public boolean isEmpty() {
        return contents.isEmpty();
    }

    @Override public boolean contains(Object o) {
        return contents.contains(o);
    }

    @Override public Iterator<Region> iterator() {
        return contents.iterator();
    }

    @Override public Object[] toArray() {
        return contents.toArray();
    }

    @Override public <T> T[] toArray(T[] a) {
        return contents.toArray(a);
    }

    @Override public boolean remove(Object o) {
        if (contents.remove(o)) {
            if (o instanceof Region) {
                TownshipsRegion r = (TownshipsRegion) o;
                r.getBoundingAreas().remove(this);
            } else if (o instanceof RegionBoundingArea) {
                TownshipsRegion r = (TownshipsRegion) ((RegionBoundingArea) o).getRegion();
                r.getBoundingAreas().remove(this);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override public boolean containsAll(Collection<?> c) {
        return contents.containsAll(c);
    }

    @Override public boolean addAll(Collection<? extends Region> c) {
        return contents.addAll(c);
    }

    @Override protected void constructContainedRegions(Set<Region> regions) {
        regions.addAll(contents);
    }

    @Override public Optional<Area> getBoundingArea(int x, int z) {
        return Optional.of((Area) this); // Not sure why this cast is needed but compiler complains
    }

    @Override public void getIntersectingRegions(BoundingArea bounds, TreeSet<Region> col) {
        for (Region r : contents) {
            if (bounds.intersects(r.getBounds()) && !bounds.equals(r.getBounds())) {
                col.add(r);
            }
        }
    }

    @Override public boolean removeAll(Collection<?> c) {
        return contents.removeAll(c);
    }

    @Override public boolean retainAll(Collection<?> c) {
        return contents.retainAll(c);
    }

    @Override public void clear() {
        contents.clear();
    }

    @Override public Collection<Citizen> getCitizensInArea() {
        return currCitizens;
    }

}
