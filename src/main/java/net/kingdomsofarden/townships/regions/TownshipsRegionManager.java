package net.kingdomsofarden.townships.regions;

import com.google.common.base.Optional;
import net.kingdomsofarden.townships.api.regions.Area;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.regions.RegionManager;
import net.kingdomsofarden.townships.regions.collections.QuadrantBoundCollection;
import net.kingdomsofarden.townships.regions.collections.RegionBoundCollection;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

public class TownshipsRegionManager implements RegionManager {

    private Map<UUID, RegionBoundCollection> maps; // One per world
    private Map<UUID, Region> uidToRegion;
    private Map<String, UUID> nameToUid;

    @Override
    public int size() {
        return uidToRegion.size();
    }

    @Override
    public boolean isEmpty() {
        return uidToRegion.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        if (o instanceof String) {
            return nameToUid.containsKey(((String) o).toLowerCase())
                    && uidToRegion.containsKey(nameToUid.get(((String) o).toLowerCase()));
        } else if (o instanceof UUID) {
            return uidToRegion.containsKey(o);
        } else if (o instanceof Region) {
            return uidToRegion.containsValue(o);
        } else {
            return false;
        }
    }

    @Override
    public Iterator<Region> iterator() {
        return uidToRegion.values().iterator();
    }

    @Override
    public Object[] toArray() {
        return uidToRegion.values().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return uidToRegion.values().toArray(a);
    }

    @Override
    public boolean add(Region region) {
        UUID id = region.getUid();
        uidToRegion.put(id, region);
        if (region.getName().isPresent()) {
            nameToUid.put(region.getName().get().toLowerCase(), id);
        }
        UUID world = region.getLocation().getWorld().getUID();
        if (maps.containsKey(world)) {
            return maps.get(world).add(region);
        } else {
            int[] borders = get2dBorders(region.getLocation().getWorld());
            maps.put(world, new QuadrantBoundCollection(-1, null, borders[0], borders[1], borders[2], borders[3]));
            return maps.get(world).add(region);
        }
    }

    private int[] get2dBorders(World world) {
        return new int[4]; //TODO Load from Configs
    }

    @Override
    public boolean remove(Object o) {
        Region r;
        if (o instanceof String) {
            r = nameToUid.containsKey(((String) o).toLowerCase()) ?
                    uidToRegion.get(nameToUid.get(((String) o).toLowerCase())) : null;
        } else if (o instanceof UUID) {
            r = uidToRegion.get(o);
        } else if (o instanceof Region) {
            r = (Region) o;
        } else {
            return false;
        }
        UUID id = r.getUid();
        uidToRegion.remove(id);
        if (r.getName().isPresent()) {
            nameToUid.remove(r.getName().get().toLowerCase());
        }
        UUID world = r.getLocation().getWorld().getUID();
        return maps.containsKey(world) && maps.get(world).remove(r);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return uidToRegion.keySet().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Region> c) {
        boolean modify = false;
        for (Region r : c) {
            if (add(r)) {
                modify = true;
            }
        }
        return modify;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modify = false;
        for (Object o : c) {
            if (remove(o)) {
                modify = true;
            }
        }
        return modify;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modify = false;
        for (Object o : c) {
            if (!contains(o)) {
                if (remove(o)) {
                    modify = true;
                }
            }
        }
        return modify;
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Flushing the Region Manager is not a permitted operation");
    }

    @Override
    public Collection<Region> getBoundingRegions(Location loc) {
        UUID world = loc.getWorld().getUID();
        if (maps.containsKey(world)) {
            return maps.get(world).getBoundingRegions(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        } else {
            return new LinkedList<Region>();
        }
    }

    @Override
    public Optional<Area> getBoundingArea(Location loc) {
        return null; //TODO
    }
}
