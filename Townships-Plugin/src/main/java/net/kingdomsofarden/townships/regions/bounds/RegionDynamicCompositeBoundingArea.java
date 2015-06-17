package net.kingdomsofarden.townships.regions.bounds;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.kingdomsofarden.townships.ThreadManager;
import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.api.math.Geometry;
import net.kingdomsofarden.townships.api.math.Line3I;
import net.kingdomsofarden.townships.api.math.Point3I;
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
import java.util.stream.Collectors;

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

    private Map<String, Collection<Region>> contents;
    private RegionBoundingArea flattenedBounds;



    public void add(Region region) {
        RegionBoundingArea grow = region.getBounds().grow(RegionBoundingArea.class, buffer);
        boundsMap.add(grow);
        flattened.add((RegionBoundingArea) grow.flatten());
        // Remove existing vertices that intersect with what we are adding
        Iterator<Point3I> it = vertices.iterator();
        while (it.hasNext()) {
            Point3I next = it.next();
            if (grow.isInBounds(next.getX(), next.getY(), next.getZ())) {
                it.remove();
            }
        }
        // Get vertices from our new addition that do not intersect with
        // what we have already and add to our current vertices
        vertices.addAll(grow.getBoundGeometry().getVertices().stream().filter(
            vec -> boundsMap.getBoundingRegions(vec.getX(), vec.getY(), vec.getZ()).isEmpty())
            .collect(Collectors.toList()));
        // Readd intersections
        for (RegionBoundingArea intersect : boundsMap.getIntersectingBounds(grow)) {
            for (Line3I edge : intersect.getBoundGeometry().getAllEdges()) {
                for (Line3I edge2 : grow.getBoundGeometry().getAllEdges()) {
                    Point3I add;
                    if ((add = edge.getIntersection(edge2)) != null) {
                        vertices.add(add);
                    }
                }
            }
        }

    }

    public void remove(Region region) {
        boundsMap.remove(region.getBounds().grow(RegionBoundingArea.class, buffer));
        // TODO: recalculate geometries
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
        // TODO
        return null;
    }
    //
    //    @Override public Collection<Point3I> getVertices() {
    //        if (!vectorCacheValid) {
    //            HashMap<Region, Collection<Point3I>> vertexConstruction =
    //                new HashMap<Region, Collection<Point3I>>();
    //            for (RegionBoundingArea b : boundsMap.getContainedBounds()) {
    //                vertexConstruction.put(b.getRegion(), b.getVertices());
    //            }
    //            // Remove intersections
    //            for (Entry<Region, Collection<Point3I>> e : vertexConstruction.entrySet()) {
    //                Iterator<Point3I> i = e.getValue().iterator();
    //                while (i.hasNext()) {
    //                    Point3I vertex = i.next();
    //                    TreeSet<Region> bounds =
    //                        boundsMap.getBoundingRegions(vertex.getX(), vertex.getY(), vertex.getZ());
    //                    bounds.remove(e.getKey());
    //                    if (!bounds.isEmpty()) { // Intersects with something else, can't be a vertex
    //                        i.remove();
    //                    }
    //                }
    //            }
    //            Collection<Point3I> intersections = buildIntersections();
    //            Iterator<Point3I> i = intersections.iterator();
    //            while (i.hasNext()) {
    //                Point3I vertex = i.next();
    //                TreeSet<Region> bounds =
    //                    boundsMap.getBoundingRegions(vertex.getX(), vertex.getY(), vertex.getZ());
    //                if (bounds.size() != 1) { // Intersects with something else, can't be a vertex
    //                    i.remove();
    //                }
    //            }
    //            // Create composite vertex collection
    //            Collection<Point3I> ret = new LinkedList<Point3I>();
    //            for (Entry<Region, Collection<Point3I>> e : vertexConstruction.entrySet()) {
    //                ret.addAll(e.getValue());
    //            }
    //            ret.addAll(intersections);
    //            vectorCacheValid = true;
    //            vertices = ret;
    //        }
    //        return vertices;
    //    }

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

    //    public Collection<Point3I> buildIntersections() {
    //        Set<Point3I> intersections = new HashSet<Point3I>();
    //        for (Entry<Region, Map<Collection<Line3I>, Collection<Region>>> entry : vectors
    //            .entrySet()) {
    //            for (Entry<Collection<Line3I>, Collection<Region>> e : entry.getValue().entrySet()) {
    //                for (Line3I line : e.getKey()) {
    //                    for (Region region : e.getValue()) {
    //                        for (Line3I line2 : region.getBounds().grow(BoundingArea.class,
    //                            buffer).getBoundGeometry().getEdges()) {
    //                            Point3I intersect = line.getIntersection(line2);
    //                            if (intersect != null) {
    //                                intersections.add(intersect);
    //                            }
    //                        }
    //                    }
    //                }
    //            }
    //        }
    //        return intersections;
    //    }

    @Override public int size2d() {
        return boundsMap.getContentSurfaceArea();
    }

    @Override public int volume() {
        return boundsMap.getContentVolume();
    }

    @Override public <T extends BoundingArea> T grow(Class<T> clazz, int size) {
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
        vertices = null;
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
            // TODO
            return null;
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

        @Override public <T extends BoundingArea> T grow(Class<T> clazz, int size) {
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
}
