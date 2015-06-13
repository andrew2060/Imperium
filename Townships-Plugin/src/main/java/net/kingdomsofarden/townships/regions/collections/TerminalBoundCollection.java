package net.kingdomsofarden.townships.regions.collections;

import com.google.common.base.Optional;
import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.math.Point3I;
import net.kingdomsofarden.townships.api.math.Rectangle;
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
        //        return 0; // TODO
        throw new UnsupportedOperationException(
            "Complex Volume Geometries are not implemented " + "yet!");
    }

    @Override public int getContentSurfaceArea() {
        if (!geometryCacheValid) {
            int sum = 0;
            Collection<Rectangle> raw = new HashSet<Rectangle>();
            // Sweep algorithm: left->right bottom->top
            for (BoundingArea bounds : flattenedBounds.values()) {
                raw.addAll(bounds.getBoundGeometry().getBaseRectangles());
            }
            RectangleXPair[] sortedPairs = new RectangleXPair[raw.size() * 2];
            int temp = -1;
            for (Rectangle rectangle : raw) {
                sortedPairs[temp++] =
                    new RectangleXPair(rectangle, Direction.BEGIN, rectangle.getStartX());
                sortedPairs[temp++] = new RectangleXPair(rectangle, Direction.END, rectangle.getEndX());
            }
            Arrays.sort(sortedPairs);
            int xStart = bounds.getMinX();
            Set<Rectangle> rectangles = new HashSet<Rectangle>();
            Deque<Rectangle> removeQueue = new LinkedList<Rectangle>();
            for (int i = 0; i < sortedPairs.length; ) {
                // Remove all preceding from queue - this is faster than removeAll because
                // removeQueue will always be smaller
                for (Rectangle rect : removeQueue) {
                    rectangles.remove(rect);
                }
                removeQueue.clear();
                RectangleXPair rect = sortedPairs[i];
                int currX = rect.value;
                if (xStart >= currX) { // We are still before/at the beginning of our bounds
                    continue;
                }
                // Get all that share this boundary
                while (rect.value == currX && i < sortedPairs.length) {
                    if (rect.direction == Direction.BEGIN) {
                        rectangles.add(rect.rectangle);
                    } else {
                        removeQueue.add(rect.rectangle);
                    }
                    i++;
                    rect = sortedPairs[i];
                }
                // Length of segment
                int xLength = currX - xStart;
                // Rerun cross sections
                int j = -1;
                RectangleZPair[] crossSectionValues = new RectangleZPair[rectangles.size() * 2];
                for (Rectangle rectangle : rectangles) {
                    crossSectionValues[++j] = new RectangleZPair(Direction.BEGIN, rectangle.getStartZ
                        ());
                    crossSectionValues[++j] = new RectangleZPair(Direction.END, rectangle.getEndZ
                        ());
                }

                Arrays.sort(crossSectionValues);
                int zLength = 0; // The total accumulated length of the rectangular cross section
                int layers = 0; // The total rectangle layers covering this area so far
                int zStart = bounds.getMinZ();

                for (RectangleZPair entry : crossSectionValues) {
                    if (entry.direction == Direction.BEGIN) {
                        if (layers == 0 && entry.value > zStart) { // Only allow positive
                            // increments of z
                            zStart = entry.value;
                        }
                        layers++;
                    } else {
                        layers--; // We sort such that begins comes before
                        if (layers == 0) {
                            if (entry.value > bounds.getMaxZ()) {
                                zLength += bounds.getMaxZ() - zStart;
                                break; // We are done/have hit the end
                            } else {
                                zLength += entry.value - zStart; // Add to cross section
                                zStart = entry.value + 1;
                            }
                        }
                    }
                }
                sum += zLength * xLength;
            }
            area = sum;
            geometryCacheValid = true;
        }
        return area;
    }

    private static class RectangleZPair implements Comparable<RectangleZPair> {
        private Direction direction;
        private int value;


        private RectangleZPair(Direction direction, int value) {
            this.direction = direction;
            this.value = value;
        }

        @Override public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            RectangleZPair that = (RectangleZPair) o;

            if (value != that.value)
                return false;
            return direction == that.direction;

        }

        @Override public int hashCode() {
            int result = direction != null ? direction.hashCode() : 0;
            result = 31 * result + value;
            return result;
        }

        @Override public int compareTo(RectangleZPair o) {
            int ret = value - o.value;
            if (ret == 0) {
                return direction.compareTo(o.direction);
            } else {
                return ret;
            }
        }

    }

    private enum Direction implements Comparable<Direction> {
        BEGIN, END
    }

    private static class RectangleXPair implements Comparable<RectangleXPair> {
        Direction direction;
        Rectangle rectangle;
        int value;

        public RectangleXPair(Rectangle rectangle, Direction direction, int value) {
            this.rectangle = rectangle;
            this.value = value;
            this.direction = direction;
        }

        @Override public int compareTo(RectangleXPair o) {
            int ret = value - o.value;
            if (ret == 0) {
                return direction.compareTo(o.direction);
            }
            return ret;
        }

        @Override public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            RectangleXPair pair = (RectangleXPair) o;

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

