package net.kingdomsofarden.townships.regions;

import com.google.common.base.Optional;
import net.kingdomsofarden.townships.TownshipsPlugin;
import net.kingdomsofarden.townships.api.effects.Effect;
import net.kingdomsofarden.townships.api.effects.TickableEffect;
import net.kingdomsofarden.townships.api.regions.Area;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.regions.RegionManager;
import net.kingdomsofarden.townships.api.regions.bounds.BoundingBox;
import net.kingdomsofarden.townships.regions.collections.AxisBoundCollection;
import net.kingdomsofarden.townships.regions.collections.RegionBoundCollection;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;

public class TownshipsRegionManager implements RegionManager {

    private TownshipsPlugin plugin;
    private Map<UUID, RegionBoundCollection> maps; // One per world
    private Map<UUID, Region> uidToRegion;
    private Map<String, UUID> nameToUid;

    public TownshipsRegionManager(TownshipsPlugin plugin) {
        this.plugin = plugin;
        this.maps = new HashMap<UUID, RegionBoundCollection>();
        this.uidToRegion = new HashMap<UUID, Region>();
        this.nameToUid = new HashMap<String, UUID>();
    }

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
        } else {
            return o instanceof Region && uidToRegion.containsValue(o);
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
        for (Effect effect : region.getEffects()) {
            if (effect instanceof TickableEffect) {
                plugin.getEffectManager().getEffectTaskManager().schedule((TickableEffect) effect, region);
            }
        }
        UUID id = region.getUid();
        uidToRegion.put(id, region);
        if (region.getName().isPresent()) {
            nameToUid.put(region.getName().get().toLowerCase(), id);
        }
        World world = region.getBounds().getWorld();
        UUID wUid = world.getUID();
        boolean ret;
        if (!maps.containsKey(wUid)) {
            maps.put(wUid, new AxisBoundCollection(world, true));
        }
        ret =  maps.get(wUid).add(region);
        for (Region r : getIntersectingRegions(region.getBounds())) {
            if (r.getTier() > region.getTier()) {
                r.getChildren().add(region);
            } else if (r.getTier() < region.getTier()) {
                r.getParents().add(region);
            }
        }
        return ret;
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
        if (r == null) {
            return false;
        }
        for (Effect effect : r.getEffects()) {
            if (effect instanceof TickableEffect) {
                plugin.getEffectManager().getEffectTaskManager().unschedule((TickableEffect) effect);
            }
        }
        UUID id = r.getUid();
        uidToRegion.remove(id);
        if (r.getName().isPresent()) {
            nameToUid.remove(r.getName().get().toLowerCase());
        }
        plugin.getStorage().removeRegion(id);
        UUID world = r.getBounds().getWorld().getUID();
        r.setValid(false);
        boolean ret = maps.containsKey(world) && maps.get(world).remove(r);
        if (ret) {
            for (Region region : getIntersectingRegions(r.getBounds())) {
                if (region.getTier() > r.getTier()) {
                    region.getChildren().remove(r);
                } else if (region.getTier() < r.getTier()) {
                    region.getParents().remove(r);
                }
            }
        }
        return ret;
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
    public TreeSet<Region> getBoundingRegions(Location loc) {
        UUID world = loc.getWorld().getUID();
        if (maps.containsKey(world)) {
            return maps.get(world).getBoundingRegions(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        } else {
            return new TreeSet<Region>();
        }
    }

    @Override
    public Optional<Area> getBoundingArea(Location loc) {
        RegionBoundCollection col = maps.get(loc.getWorld().getUID());
        return col != null ? col.getBoundingArea(loc.getBlockX(), loc.getBlockZ()) : Optional.<Area>absent();
    }

    @Override
    public TreeSet<Region> getIntersectingRegions(BoundingBox bounds) {
        UUID world = bounds.getWorld().getUID();
        if (maps.containsKey(world)) {
            TreeSet<Region> ret = new TreeSet<Region>(new Comparator<Region>() {
                @Override
                public int compare(Region o1, Region o2) {
                    int ret = o2.getTier() - o1.getTier();
                    if (ret == 0) {
                        return o1.getUid().compareTo(o2.getUid());
                    } else {
                        return ret;
                    }
                }
            });
            maps.get(world).getIntersectingRegions(bounds, ret);
            return ret;
        }
        return new TreeSet<Region>();
    }

    @Override
    public Optional<Region> get(String name) {
        UUID id = nameToUid.get(name.toLowerCase());
        if (id != null)
            return Optional.fromNullable(uidToRegion.get(id));
        else
            return Optional.absent();
    }

    @Override
    public Optional<Region> get(UUID uuid) {
        return Optional.fromNullable(uidToRegion.get(uuid));
    }

}