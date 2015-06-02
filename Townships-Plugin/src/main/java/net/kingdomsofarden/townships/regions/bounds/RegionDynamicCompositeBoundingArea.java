package net.kingdomsofarden.townships.regions.bounds;

import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.api.math.Line3I;
import net.kingdomsofarden.townships.api.math.Vector3I;
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
    private Collection<Vector3I> vertices;
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
        contents = new HashMap<String, Collection<Region>>();
        region = r;
        center = c;
        new GrowthRunnable(center).run();
    }

    public void add(Region region) {
        RegionBoundingArea grow = region.getBounds().grow(buffer);
        boundsMap.add(grow);
        // Remove existing vertices that intersect with what we are adding
        Iterator<Vector3I> it = vertices.iterator();
        while (it.hasNext()) {
            Vector3I next = it.next();
            if (grow.isInBounds(next.getX(), next.getY(), next.getZ())) {
                it.remove();
            }
        }
        // Get vertices from our new addition that do not intersect with
        // what we have already and add to our current vertices
        for (Vector3I vec : grow.getVertices()) {
            if (boundsMap.getBoundingRegions(vec.getX(), vec.getY(), vec.getZ()).isEmpty()) {
                vertices.add(vec);
            }
        }
        // Readd intersections
        for (RegionBoundingArea intersect : boundsMap.getIntersectingBounds(grow)) {
            for (Line3I edge : intersect.getEdges()) {
                for (Line3I edge2 : grow.getEdges()) {
                    Vector3I add;
                    if ((add = edge.getIntersection(edge2)) != null) {
                        vertices.add(add);
                    }
                }
            }
        }

    }

    public void remove(Region region) {
        boundsMap.remove(region.getBounds().grow(buffer));
    }

    @Override public boolean isInBounds(Location loc) {
        return boundsMap.isInBounds(loc);
    }

    @Override public boolean isInBounds(double x, double y, double z) {
        return boundsMap.isInBounds(new Location(world, x, y, z));
    }

    @Override public boolean intersects(BoundingArea box) {
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
        boundsMap.getIntersectingRegions(box, ret);
        return !ret.isEmpty() && !box.encapsulates(this);
    }

    @Override public Collection<Vector3I> getVertices() {
        if (!vectorCacheValid) {
            HashMap<Region, Collection<Vector3I>> vertexConstruction =
                new HashMap<Region, Collection<Vector3I>>();
            for (RegionBoundingArea b : boundsMap.getContainedBounds()) {
                vertexConstruction.put(b.getRegion(), b.getVertices());
            }
            // Remove intersections
            for (Entry<Region, Collection<Vector3I>> e : vertexConstruction.entrySet()) {
                Iterator<Vector3I> i = e.getValue().iterator();
                while (i.hasNext()) {
                    Vector3I vertex = i.next();
                    TreeSet<Region> bounds =
                        boundsMap.getBoundingRegions(vertex.getX(), vertex.getY(), vertex.getZ());
                    bounds.remove(e.getKey());
                    if (!bounds.isEmpty()) { // Intersects with something else, can't be a vertex
                        i.remove();
                    }
                }
            }
            Collection<Vector3I> intersections = buildIntersections();
            Iterator<Vector3I> i = intersections.iterator();
            while (i.hasNext()) {
                Vector3I vertex = i.next();
                TreeSet<Region> bounds =
                    boundsMap.getBoundingRegions(vertex.getX(), vertex.getY(), vertex.getZ());
                if (bounds.size() != 1) { // Intersects with something else, can't be a vertex
                    i.remove();
                }
            }
            // Create composite vertex collection
            Collection<Vector3I> ret = new LinkedList<Vector3I>();
            for (Entry<Region, Collection<Vector3I>> e : vertexConstruction.entrySet()) {
                ret.addAll(e.getValue());
            }
            ret.addAll(intersections);
            vectorCacheValid = true;
            vertices = ret;
        }
        return vertices;
    }

    @Override public World getWorld() {
        return world;
    }

    @Override public boolean encapsulates(BoundingArea other) {
        for (Vector3I vertex : other.getVertices()) {
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

    public Collection<Vector3I> buildIntersections() {
        Set<Vector3I> intersections = new HashSet<Vector3I>();
        for (Entry<Region, Map<Collection<Line3I>, Collection<Region>>> entry : vectors
            .entrySet()) {
            for (Entry<Collection<Line3I>, Collection<Region>> e : entry.getValue().entrySet()) {
                for (Line3I line : e.getKey()) {
                    for (Region region : e.getValue()) {
                        for (Line3I line2 : region.getBounds().grow(buffer).getEdges()) {
                            Vector3I intersect = line.getIntersection(line2);
                            if (intersect != null) {
                                intersections.add(intersect);
                            }
                        }
                    }
                }
            }
        }
        return intersections;
    }

    @Override public int size2d() {
        if (sizeCacheValid) {
            return size;
        } else {
            // Get vectors
            ArrayList<Vector3I> vertices = flatten(getVertices());
            // Calculate area by abs((x1y2-y1x2)+(x2y3-y2x3)...+(xny1-ynx1))/2 for consecutive vertice pairs
            int one = 0;
            int two = 1;
            int max = vertices.size();
            int sum = 0;
            while (one != max) {
                Vector3I v1 = vertices.get(one);
                Vector3I v2 = vertices.get(two);
                sum += v1.getX() * v2.getZ() - v1.getZ() * v2.getX();
                one++;
                two++;
                if (two == max) {
                    two = 0;
                }
            }
            sum = Math.abs(sum);
            sum /= 2;
            size = sum;
            sizeCacheValid = true;
            return size;
        }
    }

    // Flattens into a 2d structure
    private ArrayList<Vector3I> flatten(Collection<Vector3I> vertices) {
        HashSet<Vector3I> coll = new LinkedHashSet<Vector3I>();
        for (Vector3I vec : vertices) {
            coll.add(new Vector3I(vec.getX(), 0, vec.getZ()));
        }
        // TODO: remove inner vertices
        return new ArrayList<Vector3I>(coll);
    }

    @Override public int volume() {
        if (!volumeCacheValid) {
            volume = size2d() * 256; // Composite bounding regions are all 256 blocks
            volumeCacheValid = true;
        }
        return volume;
    }

    @Override public <T extends BoundingArea> T grow(int size) {
        return (T) new RegionDynamicCompositeBoundingArea(buffer + size, world, region, center);
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
                RegionBoundingArea grown = region.getBounds().grow(buffer);
                for (RegionBoundingArea r : Townships.getRegions().getIntersectingBounds(grown)) {
                    if (!this.processed.contains(r.getRegion()) && !temp.contains(r.getRegion())) {
                        temp.add(r.getRegion());
                    }
                }
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
