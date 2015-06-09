package net.kingdomsofarden.townships.regions.collections;

import com.google.common.base.Optional;
import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.math.*;
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

    private Map<Region, RegionBoundingArea> contents;
    // TODO: use heap/binary tree instead for most efficient searching for a specific region?
    private Collection<Citizen> currCitizens;
    private Collection<Point3I> vertices;
    private Map<Region, BoundingArea> flattenedBounds;
    private Collection<Point3I> verticesFlattened;

    private boolean geometryCacheValid;
    private int area;

    public TerminalBoundCollection(World world, int xLeft, int xRight, int zLower, int zUpper) {
        this.contents = new HashMap<Region, RegionBoundingArea>();
        this.flattenedBounds = new HashMap<Region, BoundingArea>();
        this.minX = xLeft;
        this.maxX = xRight;
        this.minZ = zLower;
        this.maxZ = zUpper;
        this.currCitizens = new HashSet<Citizen>();
        this.world = world;
        this.bounds = new AreaBoundingBox(world, minX, maxX, minZ, maxZ);
        this.vertices = new HashSet<Point3I>();
        this.verticesFlattened = new HashSet<Point3I>();
        this.geometryCacheValid = false;
        this.area = 0;
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
        for (RegionBoundingArea r : contents.values()) {
            if (r.isInBounds(x, y, z)) {
                ret.add(r.getRegion());
            }
        }
        return ret;
    }

    // Inherited Stuff

    @Override public boolean add(RegionBoundingArea bound) {
        TownshipsRegion r = (TownshipsRegion) bound.getRegion();
        r.getBoundingAreas().add(this);
        flattenedBounds.put(r, bound.flatten());
        // Add vertices to what we are tracking
        //        for (Point3I vertex : bound.getVertices()) {
        //            if (bounds.isInBounds(vertex.getX(), vertex.getY(), vertex.getZ())) {
        //                vertices.add(vertex);
        //                verticesFlattened.add(new Point3I(vertex.getX(), 0, vertex.getZ()));
        //            }
        //        }
        return contents.put(r, bound) != bound;
    }

    @Override public int size() {
        return contents.size();
    }

    @Override public boolean isEmpty() {
        return contents.isEmpty();
    }

    @Override public boolean contains(Object o) {
        if (o instanceof Region) {
            return contents.containsKey(o);
        } else if (o instanceof RegionBoundingArea) {
            return contents.containsValue(o);
        } else {
            return false;
        }
    }

    @Override public Iterator<Region> iterator() {
        return contents.keySet().iterator();
    }

    @Override public Object[] toArray() {
        return contents.keySet().toArray();
    }

    @Override public <T> T[] toArray(T[] a) {
        return contents.keySet().toArray(a);
    }

    @Override public boolean remove(Object o) {
        if (o instanceof Region) {
            if (contents.remove(o) != null) {
                TownshipsRegion r = (TownshipsRegion) o;
                r.getBoundingAreas().remove(this);
                flattenedBounds.remove(r);
                return true;
            }
        } else if (o instanceof RegionBoundingArea) {
            if (contents.remove(((RegionBoundingArea) o).getRegion()) != null) {
                TownshipsRegion r = (TownshipsRegion) ((RegionBoundingArea) o).getRegion();
                r.getBoundingAreas().remove(this);
                flattenedBounds.remove(r);
                return true;
            }
        }
        return false;
    }

    @Override public boolean containsAll(Collection<?> c) {
        return contents.keySet().containsAll(c);
    }

    @Override public boolean addAll(Collection<? extends Region> c) {
        return contents.keySet().addAll(c);
    }

    @Override protected void constructContainedRegions(Set<Region> regions) {
        regions.addAll(contents.keySet());
    }

    @Override public Optional<Area> getBoundingArea(int x, int z) {
        return Optional.of((Area) this); // Not sure why this cast is needed but compiler complains
    }

    @Override public void getIntersectingRegions(BoundingArea bounds, TreeSet<Region> col) {
        for (RegionBoundingArea r : contents.values()) {
            if (bounds.intersects(r) && !bounds.equals(r)) {
                col.add(r.getRegion());
            }
        }
    }

    @Override public Collection<RegionBoundingArea> getContainedBounds() {
        return contents.values();
    }

    @Override
    public Collection<RegionBoundingArea> getIntersectingBounds(RegionBoundingArea bounds) {
        return null;
    }

    @Override public boolean removeAll(Collection<?> c) {
        boolean mod = false;
        for (Object obj : c) {
            if (remove(obj)) {
                mod = true;
            }
        }
        return mod;
    }

    @Override public boolean retainAll(Collection<?> c) {
        boolean mod = false;
        for (Object o : c) {
            if (!contains(o)) {
                if (remove(o)) {
                    mod = true;
                }
            }
        }
        return mod;
    }

    @Override public void clear() {
        contents.clear();
    }

    @Override public Collection<Citizen> getCitizensInArea() {
        return currCitizens;
    }

    @Override public int getContentVolume() {
        //        for (RegionBoundingArea bounds : contents.values()) {
        //            Geometry geometry = bounds.getBoundGeometry();
        //            for (Point3I vertex : geometry.getVertices()) {
        //                LinkedList<HalfFace> faces = new LinkedList<HalfFace>();
        //                ArrayList<Line3I> localEdges = new ArrayList<Line3I>(geometry.getEdges(vertex));
        //                // Get all pairs of two and find faces
        //                for (int i = 0; i < localEdges.size(); i++) {
        //                    for (int j = 1; j < localEdges.size(); j++) {
        //                        Axis l1Axis = localEdges.get(i).getAxisOfTravel();
        //                        Axis l2Axis = localEdges.get(j).getAxisOfTravel();
        //                        if (l1Axis.asIntValue() != l2Axis.asIntValue()) {
        //                            int intVal = 6 - (l1Axis.asIntValue() + l2Axis.asIntValue());
        //                            Axis perpendicularAxis = Axis.fromIntValue(intVal);
        //                            Plane plane = new Plane(perpendicularAxis, intVal == 1 ? vertex.getX() :
        //                                intVal == 2 ? vertex.getY() : vertex.getZ());
        //                            faces.add(new HalfFace(plane, localEdges.get(i).asVector(vertex),
        //                                localEdges.get(i).asVector(vertex)));
        //                        }
        //                    }
        //                }
        //            }
        //        }
        //        return 0;
        throw new UnsupportedOperationException("Complex Volume Geometries are not implemented "
            + "yet!");
    }

    @Override public int getContentSurfaceArea() {
        if (!geometryCacheValid) {
            Collection<Rectangle> raw = new HashSet<Rectangle>();
            // Sweep algorithm: left->right bottom->top
            for (BoundingArea bounds : flattenedBounds.values()) {
                raw.addAll(bounds.getRawRectangularGeometry().getBaseRectangles());
            }
            Pair[] startXColl = new Pair[raw.size()];
            Pair[] endXColl = new Pair[raw.size()];
            int temp = 0;
            for (Rectangle rectangle : raw) {
                startXColl[temp] = new Pair(rectangle, rectangle.getStartX());
                endXColl[temp] = new Pair(rectangle, rectangle.getEndX());
            }
            Arrays.sort(startXColl);
            Arrays.sort(endXColl);
            int xStartIdx = 0;
            int xEndIdx = 0;
            // Construct preexisting on left side too, ending edges need to trail by 1
            Set<Rectangle> rectangles = new HashSet<Rectangle>();
            while (startXColl[xStartIdx].value < bounds.getMinX() && xStartIdx < startXColl.length) {
                xStartIdx++;
                rectangles.add(startXColl[xStartIdx].rectangle);
            }
            while (endXColl[xEndIdx].value < bounds.getMaxX() && xEndIdx < endXColl.length) {
                xEndIdx++;
                rectangles.add(endXColl[xEndIdx].rectangle);
            }
            int sum = 0;
            Deque<Rectangle> remove = new LinkedList<Rectangle>();
            for (int i = bounds.getMinX(); i <= bounds.getMaxX(); i++) {
                rectangles.removeAll(remove);
                remove.clear();
                Pair curr;
                while ((curr = startXColl[xStartIdx]).value == i) {
                    rectangles.add(curr.rectangle);
                    xStartIdx++;
                }
                while ((curr = endXColl[xEndIdx]).value == i) {
                    remove.push(curr.rectangle);
                    xEndIdx++;
                }
                int[] startZ = new int[rectangles.size()];
                int[] endZ = new int[rectangles.size()];
                int zStartIdx = 0;
                int zEndIdx = 0;
                int j = 0;
                for (Rectangle rectangle : rectangles) {
                    startZ[j] = rectangle.getStartZ();
                    endZ[j] = rectangle.getEndZ();
                    j++;
                }
                Arrays.sort(startZ);
                Arrays.sort(endZ);
                int overlap = 0;
                int len = 0;
                int removals = 0;
                for (int z = startZ[0]; z <= endZ[endZ.length - 1]; z++) {
                    overlap -= removals;
                    removals = 0;
                    while (startZ[zStartIdx] == z) {
                        overlap++;
                        zStartIdx++;
                    }
                    while (endZ[zEndIdx] == z) {
                        removals++;
                        zEndIdx++;
                    }
                    if (overlap > 0) {
                        len++;
                    }
                }
                sum += len;
            }
            area = sum;
        }
        return area;
    }

    private class Pair implements Comparable<Pair> {
        Rectangle rectangle;
        int value;

        public Pair(Rectangle rectangle, int value) {
            this.rectangle = rectangle;
            this.value = value;
        }

        @Override public int compareTo(Pair o) {
            return value - o.value;
        }

        @Override public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            Pair pair = (Pair) o;

            if (value != pair.value)
                return false;
            return !(rectangle != null ?
                !rectangle.equals(pair.rectangle) :
                pair.rectangle != null);

        }

        @Override public int hashCode() {
            int result = rectangle != null ? rectangle.hashCode() : 0;
            result = 31 * result + value;
            return result;
        }
    }

}

