package net.kingdomsofarden.townships.regions.bounds;

import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.api.math.Geometry;
import net.kingdomsofarden.townships.api.math.Line3I;
import net.kingdomsofarden.townships.api.math.Point3I;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.regions.bounds.BoundingArea;
import net.kingdomsofarden.townships.api.regions.bounds.CompositeBoundingArea;
import net.kingdomsofarden.townships.api.regions.bounds.RegionBoundingArea;
import net.kingdomsofarden.townships.regions.collections.AxisBoundCollection;
import net.kingdomsofarden.townships.regions.collections.RegionBoundCollection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.*;
import java.util.Map.Entry;

public class RegionDynamicCompositeBoundingArea implements CompositeBoundingArea {

    private RegionBoundCollection boundsMap;
    private int buffer;
    private World world;
    private Region region;
    private Region center;

    private boolean sizeCacheValid;
    private boolean vectorCacheValid;
    private boolean volumeCacheValid;
    private Collection<Point3I> vertices;
    private Map<Region, Map<Collection<Line3I>, Collection<Region>>> vectors;
    // A map of region to its edges/colliding regions

    // Cache these upon region addition because expensive
    private int size;
    private int volume;

    private Map<String, Collection<Region>> contents;

    public RegionDynamicCompositeBoundingArea(int amt, World w, Region r, Region c) {
        buffer = amt;
        world = w;
        boundsMap = new AxisBoundCollection(world, true);
        vertices = null;
        sizeCacheValid = false;
        vectorCacheValid = false;
        volumeCacheValid = false;
        contents = new HashMap<>();
        region = r;
        center = c;
        new GrowthRunnable(center).run();
    }

    public void add(Region region) {
        RegionBoundingArea grow = region.getBounds().grow(RegionBoundingArea.class, buffer);
        boundsMap.add(grow);
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
        for (Point3I vec : grow.getBoundGeometry().getVertices()) {
            if (boundsMap.getBoundingRegions(vec.getX(), vec.getY(), vec.getZ()).isEmpty()) {
                vertices.add(vec);
            }
        }
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
        boundsMap.remove(region.getBounds().grow
            (RegionBoundingArea.class, buffer));
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
        if (!sizeCacheValid) {
            size = boundsMap.getContentSurfaceArea();
            sizeCacheValid = true;
        }
        return size;
    }

    @Override public int volume() {
        if (!volumeCacheValid) {
            volume = boundsMap.getContentVolume();
            volumeCacheValid = true;
        }
        return volume;
    }

    @Override public <T extends BoundingArea> T grow(Class<T> clazz, int size) {
        return (T) new RegionDynamicCompositeBoundingArea(buffer + size, world, region, center);
    }

    @Override public BoundingArea flatten() {
        // TODO
        return null;
    }

    @Override public Region getRegion() {
        return region;
    }

    @Override public Map<String, Collection<Region>> getContents() {
        return contents;
    }


    private class GrowthRunnable implements Runnable {
        private HashSet<Region> processed;
        private Deque<Region> lastIteration;

        public GrowthRunnable(Region center) {
            processed = new HashSet<Region>();
            lastIteration = new LinkedList<Region>();
            lastIteration.add(center);
            size = 0;
            volume = 0;
        }

        @Override public void run() {
            Region next;
            Set<Region> temp = new HashSet<Region>();
            while ((next = lastIteration.poll()) != null) {
                processed.add(next);
                RegionBoundingArea grown = region.getBounds().grow(RegionBoundingArea.class,
                    buffer);
                Townships.getRegions().getIntersectingBounds(grown).stream().filter(
                    r -> !this.processed.contains(r.getRegion()) && !temp.contains(r.getRegion()))
                    .forEach(r -> temp.add(r.getRegion()));
            }
            if (!lastIteration.isEmpty()) {
                temp.addAll(lastIteration);
            }
            if (!temp.isEmpty()) {
                lastIteration = new LinkedList<Region>(temp);
                run();
            } else {
                for (Region region : processed) {
                    add(region);
                }
            }
        }
    }
}
