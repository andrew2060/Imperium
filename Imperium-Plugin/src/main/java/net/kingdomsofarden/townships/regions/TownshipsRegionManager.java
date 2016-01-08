package net.kingdomsofarden.townships.regions;

import com.google.common.base.Optional;
import net.kingdomsofarden.townships.TownshipsPlugin;
import net.kingdomsofarden.townships.api.effects.TickableEffect;
import net.kingdomsofarden.townships.api.regions.Area;
import net.kingdomsofarden.townships.api.regions.FunctionalRegion;
import net.kingdomsofarden.townships.api.regions.RegionManager;
import net.kingdomsofarden.townships.api.regions.bounds.BoundingArea;
import net.kingdomsofarden.townships.api.regions.bounds.RegionBoundingArea;
import net.kingdomsofarden.townships.regions.collections.AxisBoundCollection;
import net.kingdomsofarden.townships.regions.collections.RegionBoundCollection;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TownshipsRegionManager implements RegionManager {

    private TownshipsPlugin plugin;
    private Map<UUID, RegionBoundCollection> maps; // One per world
    private Map<UUID, FunctionalRegion> uidToRegion;
    private Map<String, UUID> nameToUid;

    public TownshipsRegionManager(TownshipsPlugin plugin) {
        this.plugin = plugin;
        this.maps = new HashMap<>();
        this.uidToRegion = new HashMap<>();
        this.nameToUid = new HashMap<>();
    }

    @Override public int size() {
        return uidToRegion.size();
    }

    @Override public boolean isEmpty() {
        return uidToRegion.isEmpty();
    }

    @Override public boolean contains(Object o) {
        if (o instanceof String) {
            return nameToUid.containsKey(((String) o).toLowerCase()) && uidToRegion
                .containsKey(nameToUid.get(((String) o).toLowerCase()));
        } else if (o instanceof UUID) {
            return uidToRegion.containsKey(o);
        } else {
            return o instanceof FunctionalRegion && uidToRegion.containsValue(o);
        }
    }

    @Override public Iterator<FunctionalRegion> iterator() {
        return uidToRegion.values().iterator();
    }

    @Override public void forEach(Consumer<? super FunctionalRegion> action) {

    }

    @Override public Object[] toArray() {
        return uidToRegion.values().toArray();
    }

    @Override public <T> T[] toArray(T[] a) {
        return uidToRegion.values().toArray(a);
    }

    @Override public boolean add(FunctionalRegion region) {
        region.getEffects().stream().filter(effect -> effect instanceof TickableEffect).forEach(
            effect -> plugin.getEffectManager().getEffectTaskManager()
                .schedule((TickableEffect) effect, region));
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
        ret = maps.get(wUid).add(region);
        for (FunctionalRegion r : getIntersectingRegions(region.getBounds())) {
            if (r.getTier() > region.getTier()) {
                r.getChildren().add(region);
            } else if (r.getTier() < region.getTier()) {
                r.getParents().add(region);
            }
        }
        return ret;
    }

    @Override public boolean remove(Object o) {
        FunctionalRegion r;
        if (o instanceof String) {
            r = nameToUid.containsKey(((String) o).toLowerCase()) ?
                uidToRegion.get(nameToUid.get(((String) o).toLowerCase())) :
                null;
        } else if (o instanceof UUID) {
            r = uidToRegion.get(o);
        } else if (o instanceof FunctionalRegion) {
            r = (FunctionalRegion) o;
        } else {
            return false;
        }
        if (r == null) {
            return false;
        }
        r.getEffects().stream().filter(effect -> effect instanceof TickableEffect)
            .forEach(effect -> {
                plugin.getEffectManager().getEffectTaskManager()
                    .unschedule((TickableEffect) effect);
            });
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
            for (FunctionalRegion region : getIntersectingRegions(r.getBounds())) {
                if (region.getTier() > r.getTier()) {
                    region.getChildren().remove(r);
                } else if (region.getTier() < r.getTier()) {
                    region.getParents().remove(r);
                }
            }
        }
        return ret;
    }

    @Override public boolean containsAll(Collection<?> c) {
        return uidToRegion.keySet().containsAll(c);
    }

    @Override public boolean addAll(Collection<? extends FunctionalRegion> c) {
        boolean modify = false;
        for (FunctionalRegion r : c) {
            if (add(r)) {
                modify = true;
            }
        }
        return modify;
    }

    @Override public boolean removeAll(Collection<?> c) {
        boolean modify = false;
        for (Object o : c) {
            if (remove(o)) {
                modify = true;
            }
        }
        return modify;
    }

    @Override public boolean removeIf(Predicate<? super FunctionalRegion> filter) {
        final boolean[] modified = {false};
        new ArrayList<>(uidToRegion.values()).stream().filter(filter::test).forEach(region -> {
            uidToRegion.remove(region.getUid());
            modified[0] = true;
        });
        return modified[0];
    }

    @Override public boolean retainAll(Collection<?> c) {
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

    @Override public void clear() {
        throw new UnsupportedOperationException(
            "Flushing the Region Manager is not a permitted operation");
    }

    @Override public Spliterator<FunctionalRegion> spliterator() {
        return uidToRegion.values().spliterator();
    }

    @Override public Stream<FunctionalRegion> stream() {
        return uidToRegion.values().stream();
    }

    @Override public Stream<FunctionalRegion> parallelStream() {
        return uidToRegion.values().parallelStream();
    }

    @Override public TreeSet<FunctionalRegion> getBoundingRegions(Location loc) {
        UUID world = loc.getWorld().getUID();
        if (maps.containsKey(world)) {
            return maps.get(world)
                .getBoundingRegions(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        } else {
            return new TreeSet<>();
        }
    }

    @Override public Optional<Area> getBoundingArea(Location loc) {
        RegionBoundCollection col = maps.get(loc.getWorld().getUID());
        return col != null ?
            col.getBoundingArea(loc.getBlockX(), loc.getBlockZ()) :
            Optional.<Area>absent();
    }

    @Override public TreeSet<FunctionalRegion> getIntersectingRegions(BoundingArea bounds) {
        UUID world = bounds.getWorld().getUID();
        if (maps.containsKey(world)) {
            TreeSet<FunctionalRegion> ret = new TreeSet<>((o1, o2) -> {
                int ret1 = o2.getTier() - o1.getTier();
                if (ret1 == 0) {
                    return o1.getUid().compareTo(o2.getUid());
                } else {
                    return ret1;
                }
            });
            maps.get(world).getIntersectingRegions(bounds, ret);
            return ret;
        }
        return new TreeSet<>();
    }

    @Override public Collection<RegionBoundingArea> getIntersectingBounds(BoundingArea bounds) {
        RegionBoundCollection boundsTree = maps.get(bounds.getWorld().getUID());
        return boundsTree == null ?
            Collections.emptyList() :
            boundsTree.getIntersectingBounds(bounds);
    }

    @Override public Optional<FunctionalRegion> get(String name) {
        UUID id = nameToUid.get(name.toLowerCase());
        if (id != null)
            return Optional.fromNullable(uidToRegion.get(id));
        else
            return Optional.absent();
    }

    @Override public Optional<FunctionalRegion> get(UUID uuid) {
        return Optional.fromNullable(uidToRegion.get(uuid));
    }

}
