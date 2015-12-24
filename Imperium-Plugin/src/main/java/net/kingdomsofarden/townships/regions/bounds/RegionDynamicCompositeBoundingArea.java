package net.kingdomsofarden.townships.regions.bounds;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.kingdomsofarden.townships.ThreadManager;
import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.api.math.*;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.regions.bounds.BoundingArea;
import net.kingdomsofarden.townships.api.regions.bounds.CompositeBoundingArea;
import net.kingdomsofarden.townships.api.regions.bounds.RegionBoundingArea;
import net.kingdomsofarden.townships.effects.core.EffectPowerProjector;
import net.kingdomsofarden.townships.regions.collections.AxisBoundCollection;
import net.kingdomsofarden.townships.regions.collections.RegionBoundCollection;
import net.kingdomsofarden.townships.util.MetaKeys;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class RegionDynamicCompositeBoundingArea implements CompositeBoundingArea {

    private double power;
    private double decay;
    private RegionBoundCollection boundsMap;
    private RegionBoundCollection flattened;
    private int buffer;
    private World world;
    private Region region;
    private Region center;
    private Collection<Region> extensors;
    private Collection<Point3I> vertices;
    private Collection<Point3I> flattenedVertices;
    private Map<Point3I, Segment3I> vertexToEdgeMap;

    private Map<String, Collection<Region>> contents;
    private RegionBoundingArea flattenedBounds;
    private Geometry geometry;
    private Geometry flattenedGeometry;



    public void add(Region region) {
        RegionBoundingArea grow = region.getBounds().grow(RegionBoundingArea.class, buffer);
        boundsMap.add(grow);
        flattened.add((RegionBoundingArea) grow.flatten());
        vertices.addAll(grow.getBoundGeometry().getVertices());
        flattenedVertices.addAll(grow.flatten().getBoundGeometry().getVertices());
        // TODO edges, faces
    }

    @SuppressWarnings("SuspiciousMethodCalls") public void remove(Region region) {
        RegionBoundingArea grown = region.getBounds().grow(RegionBoundingArea.class, buffer);
        boundsMap.remove(grown);
        grown.getBoundGeometry().getVertices().stream().forEach(vertices::remove);
        grown.flatten().getBoundGeometry().getVertices().stream()
            .forEach(flattenedVertices::remove);
        recalculatePower(boundsMap.getIntersectingBounds(grown));
    }


    // Check neighboring bounds, determine if power changed
    private void recalculatePower(Collection<RegionBoundingArea> boundingAreas) {
        LinkedList<RegionBoundingArea> processing = new LinkedList<>(boundingAreas);
        Collections.sort(processing, (o1, o2) -> (int) (
            (double) o2.getRegion().getRegionalMetadata(region)
                .getOrDefault(MetaKeys.MAX_POWER, 0.00) - (double) o1.getRegion()
                .getRegionalMetadata(region).getOrDefault(MetaKeys.MAX_POWER, 0.00)));
        RegionBoundingArea bounds;
        while ((bounds = processing.poll()) != null) {
            recalculatePower(bounds, processing);
        }
    }

    private void recalculatePower(RegionBoundingArea rb, LinkedList<RegionBoundingArea> process) {
        double maxPow = 0.00;
        for (RegionBoundingArea bound : boundsMap.getIntersectingBounds(rb)) {
            double pow =
                (double) bound.getRegion().getRegionalMetadata(getRegion()).get(MetaKeys.MAX_POWER);
            if (pow > maxPow) {
                maxPow = pow;
            }
        }
        double currPower =
            (double) rb.getRegion().getRegionalMetadata(region).get(MetaKeys.MAX_POWER);
        double newMax = maxPow - decay;
        if (newMax != currPower) {
            // No path to this node anymore
            if (newMax <= 0.00) {
                rb.getRegion().getRegionalMetadata(region).remove(MetaKeys.MAX_POWER);
                boundsMap.remove(rb.getRegion());
            } else {
                // Shorter route to this node (should never happen(?))
                // or longer route to this node
                rb.getRegion().getRegionalMetadata(region).put(MetaKeys.MAX_POWER, newMax);
            }
            // Either way, recalculate children
            final boolean[] changed = new boolean[] {false};
            double comp = Math.max(newMax, currPower);
            boundsMap.getIntersectingBounds(rb).stream().forEach(b -> {
                if (!process.contains(b)
                    && (double) b.getRegion().getRegionalMetadata(region).get(MetaKeys.MAX_POWER)
                    < comp) { // Is a child that isn't already being
                    // processed
                    changed[0] = true;
                    process.add(b);
                }
            });
            if (changed[0]) { // Backing processing list is changed, resort by max power descending
                Collections.sort(process, (o1, o2) -> (int) (
                    (double) o2.getRegion().getRegionalMetadata(region)
                        .getOrDefault(MetaKeys.MAX_POWER, 0.00) - (double) o1.getRegion()
                        .getRegionalMetadata(region).getOrDefault(MetaKeys.MAX_POWER, 0.00)));
            }
        }
        // Otherwise, short circuit as we just found a new route that doesn't change our max
        // power to this node
    }

    @Override public boolean isInBounds(Location loc) {
        return boundsMap.isInBounds(loc);
    }

    @Override public boolean isInBounds(double x, double y, double z) {
        return boundsMap.isInBounds(new Location(world, x, y, z));
    }

    @Override public boolean intersects(BoundingArea box) {
        TreeSet<Region> ret = new TreeSet<Region>((o1, o2) -> {
            int ret1 = o2.getTier() - o1.getTier();
            if (ret1 == 0) {
                return o1.getUid().compareTo(o2.getUid());
            } else {
                return ret1;
            }
        });
        boundsMap.getIntersectingRegions(box, ret);
        return !ret.isEmpty() && !box.encapsulates(this);
    }

    @Override public Geometry getBoundGeometry() {
        return geometry;
    }

    @Override public World getWorld() {
        return world;
    }

    @Override public boolean encapsulates(BoundingArea other) {
        for (Point3I vertex : other.getBoundGeometry().getVertices()) {
            if (!boundsMap.isInBounds(
                new Location(other.getWorld(), vertex.getX(), vertex.getY(), vertex.getZ()))) {
                return false;
            }
        }
        for (Point3I vertex : getBoundGeometry().getVertices()) {
            if (!other.isInBounds(vertex.getX(), vertex.getY(), vertex.getZ())) {
                return false;
            }
        }
        return true;
    }

    @Override public Map<Material, Integer> checkForBlocks(Map<Material, Integer> blocks) {
        throw new UnsupportedOperationException(
            "We do NOT allow block requirements on unbounded composite regions for"
                + " performance reasons");
    }

    @Override public int size2d() {
        return boundsMap.getContentSurfaceArea();
    }

    @Override public int volume() {
        return boundsMap.getContentVolume();
    }

    @SuppressWarnings("unchecked") @Override
    public <T extends BoundingArea> T grow(Class<T> clazz, int size) {
        T ret = (T) new RegionDynamicCompositeBoundingArea();
        JsonObject init = new JsonObject();
        init.add("buffer", new JsonPrimitive(buffer + size));
        init.add("world", new JsonPrimitive(world.getUID().toString()));
        init.add("region", new JsonPrimitive(region.getUid().toString()));
        init.add("center", new JsonPrimitive(center.getUid().toString()));
        JsonArray arr = new JsonArray();
        int i = 0;
        for (Region r : extensors) {
            arr.set(i, new JsonPrimitive(r.getUid().toString()));
            i++;
        }
        init.add("extensors", arr);
        init.add("decay", new JsonPrimitive(decay));
        init.add("power", new JsonPrimitive(power));
        ret.initialize(init);
        return ret;
    }

    @Override public BoundingArea flatten() {
        return flattenedBounds;
    }

    @Override public void initialize(JsonObject json) {
        buffer = json.get("buffer").getAsInt();
        world = Bukkit.getWorld(UUID.fromString(json.get("world").getAsString()));
        boundsMap = new AxisBoundCollection(world, true);
        flattened = new AxisBoundCollection(world, true);
        vertices = new ArrayList<>();
        flattenedVertices = new ArrayList<>();
        contents = new HashMap<>();
        region =
            Townships.getRegions().get(UUID.fromString(json.get("region").getAsString())).orNull();
        center =
            Townships.getRegions().get(UUID.fromString(json.get("center").getAsString())).orNull();
        extensors = new LinkedList<>();
        for (JsonElement value : json.get("extensors").getAsJsonArray()) {
            UUID regionUID = UUID.fromString(value.getAsString());
            Region add = Townships.getRegions().get(regionUID).orNull();
            if (add != null) {
                extensors.add(add);
            }
        }
        decay = json.get("decay").getAsDouble();
        power = json.get("power").getAsDouble();
        new CompositionAggregationThread().run();
        flattenedBounds = new WrappedDynamicBoundingArea();
        geometry = new DynamicCompositeBoundingAreaGeometry(false);
        flattenedGeometry = new DynamicCompositeBoundingAreaGeometry(true);
    }

    @Override public JsonObject save() {
        JsonObject ret = new JsonObject();
        ret.addProperty("buffer", buffer);
        ret.addProperty("world", world.getUID().toString());
        ret.addProperty("region", region.getUid().toString());
        ret.addProperty("center", center.getUid().toString());
        JsonArray arr = new JsonArray();
        int i = 0;
        for (Region r : extensors) {
            arr.set(i, new JsonPrimitive(r.getUid().toString()));
            i++;
        }
        ret.add("extensors", arr);
        ret.addProperty("decay", decay);
        ret.addProperty("power", power);
        return ret;
    }

    @Override public Region getRegion() {
        return region;
    }

    @Override public Map<String, Collection<Region>> getContents() {
        return contents;
    }

    public double getDecay() {
        return decay;
    }

    /**
     * Thread handles spawning worker threads and aggregating their results to determine what
     * regions exist within this dynamic bounding area
     */
    private class CompositionAggregationThread extends Thread {
        private Map<Region, Double> visited;
        private Map<Region, CompositionWorkerThread> threadMap;
        private Set<Region> visitedNodes;

        public CompositionAggregationThread() {
            visited = new HashMap<>();
            threadMap = new HashMap<>();
            visitedNodes = new HashSet<>();
        }


        @Override public void run() {
            boundsMap.clear();
            CompositionWorkerThread t = new CompositionWorkerThread(center);
            threadMap.put(center, t);      // Centerpoint worker
            ThreadManager.getInstance().getThreadPool().submit(t);
            for (Region r : extensors) {
                t = new CompositionWorkerThread(r);
                threadMap.put(r, t);       // Extensor workers
                ThreadManager.getInstance().getThreadPool().submit(t);
            }
            // Results aggregation via traversal of nodes recursively
            appendResults(center);
            // Add to mapping
            visited.keySet().forEach(RegionDynamicCompositeBoundingArea.this::add);
            extensors = visitedNodes;
            for (Map.Entry<Region, Double> entry : visited.entrySet()) {
                entry.getKey().getRegionalMetadata(getRegion())
                    .put(MetaKeys.MAX_POWER, entry.getValue());
            }
            // TODO check ownerships of source/extensors and individual added regions
        }

        private void appendResults(Region region) {
            CompositionWorkerThread regionWorker = threadMap.get(region);
            synchronized (regionWorker.state) {
                while (!regionWorker.state.get()) {
                    try {
                        regionWorker.state.wait(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            final Map<Region, Double> accumulation = regionWorker.accumulation;
            for (Map.Entry<Region, Double> entry : accumulation.entrySet()) {
                visited.merge(entry.getKey(), entry.getValue(), Math::max);
            }
            regionWorker.nodes.forEach(node -> {
                if (!visitedNodes.contains(node)) {
                    appendResults(node);
                }
            });
        }
    }


    private class CompositionWorkerThread extends Thread {
        private final AtomicBoolean state;
        private Region center; // The center point (i.e. the power source)
        private Map<Region, Double> accumulation; // Map of accumulated region/power from worker
        private Deque<Region> processQueue;
        private HashSet<Region> nodes; // An accumulation of all other nodes within ZOC/decay
        private double currPower;

        public CompositionWorkerThread(Region region) {
            center = region;
            accumulation = new HashMap<>();
            processQueue = new LinkedList<>();
            nodes = new HashSet<>();
            processQueue.push(center);
            currPower = power;
            state = new AtomicBoolean(false);
        }

        @Override public void run() {
            Set<Region> nextIteration = new HashSet<>();
            for (Region next : processQueue) {
                Townships.getRegions()
                    .getIntersectingBounds(next.getBounds().grow(RegionBoundingArea.class, buffer))
                    .stream().filter(
                    r -> !this.accumulation.containsKey(r.getRegion()) && !nextIteration
                        .contains(r.getRegion())).forEach(r -> nextIteration.add(r.getRegion()));
            }
            if (!nextIteration.isEmpty()) {
                for (Region next : nextIteration) {
                    if (!next.hasEffect(EffectPowerProjector.NAME)) {
                        accumulation.put(next, currPower);
                    } else {
                        nodes.add(next);
                    }
                }
                currPower -= decay;
                if (currPower > 0) {
                    processQueue = new LinkedList<>(nextIteration);
                    run();
                }
            }
            state.set(true);
        }
    }


    private class WrappedDynamicBoundingArea implements RegionBoundingArea {

        @Override public Region getRegion() {
            return region;
        }

        @Override public boolean isInBounds(Location loc) {
            return flattened.isInBounds(loc);
        }

        @Override public boolean isInBounds(double x, double y, double z) {
            return flattened.getBoundingBox().isInBounds(x, y, z);
        }

        @Override public boolean intersects(BoundingArea box) {
            TreeSet<Region> ret = new TreeSet<Region>((o1, o2) -> {
                int ret1 = o2.getTier() - o1.getTier();
                if (ret1 == 0) {
                    return o1.getUid().compareTo(o2.getUid());
                } else {
                    return ret1;
                }
            });
            flattened.getIntersectingRegions(box, ret);
            return !ret.isEmpty() && !box.encapsulates(this);
        }

        @Override public Geometry getBoundGeometry() {
            return flattenedGeometry;
        }

        @Override public World getWorld() {
            return world;
        }

        @Override public boolean encapsulates(BoundingArea other) {
            for (Point3I vertex : other.getBoundGeometry().getVertices()) {
                if (!flattened.isInBounds(
                    new Location(other.getWorld(), vertex.getX(), vertex.getY(), vertex.getZ()))) {
                    return false;
                }
            }
            for (Point3I vertex : getBoundGeometry().getVertices()) {
                if (!other.isInBounds(vertex.getX(), vertex.getY(), vertex.getZ())) {
                    return false;
                }
            }
            return true;
        }

        @Override public Map<Material, Integer> checkForBlocks(Map<Material, Integer> blocks) {
            throw new UnsupportedOperationException();
        }

        @Override public int size2d() {
            return flattened.getContentSurfaceArea();
        }

        @Override public int volume() {
            return flattened.getContentVolume();
        }

        @SuppressWarnings("unchecked") @Override
        public <T extends BoundingArea> T grow(Class<T> clazz, int size) {
            return (T) RegionDynamicCompositeBoundingArea.this.grow(clazz, size).flatten();
        }

        @Override public BoundingArea flatten() {
            return this;
        }

        @Override public void initialize(JsonObject json) {
            // Do nothing
        }

        @Override public JsonObject save() {
            // Do nothing
            return null;
        }
    }


    public class DynamicCompositeBoundingAreaGeometry implements Geometry {

        boolean flag;

        public DynamicCompositeBoundingAreaGeometry() {
            this(false);
        }

        public DynamicCompositeBoundingAreaGeometry(boolean flag) {
            this.flag = flag;
        }

        @Override public Collection<Point3I> getVertices() {
            return flag ? flattenedVertices : vertices;
        }

        @Override public Collection<Segment3I> getEdges(Point3I vertex) {
            throw new UnsupportedOperationException("Not implemented yet!");
        }

        @Override public Collection<Segment3I> getAllEdges() {
            throw new UnsupportedOperationException("Not implemented yet!");
        }

        @Override public Collection<Face> getFaces() {
            throw new UnsupportedOperationException("Not implemented yet!");
        }

        @Override public Collection<Rectangle> getBaseRectangles() {
            throw new UnsupportedOperationException("Not implemented yet!");
        }
    }
}
