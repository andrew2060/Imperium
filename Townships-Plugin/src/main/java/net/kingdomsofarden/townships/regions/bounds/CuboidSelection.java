package net.kingdomsofarden.townships.regions.bounds;

import com.google.gson.JsonObject;
import net.kingdomsofarden.townships.api.math.*;
import net.kingdomsofarden.townships.api.regions.bounds.BoundingArea;
import net.kingdomsofarden.townships.api.regions.bounds.CuboidBoundingBox;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.*;

public class CuboidSelection implements CuboidBoundingBox {

    protected int maxX;
    protected int maxY;
    protected int maxZ;
    protected int minX;
    protected int minY;
    protected int minZ;
    private Location loc1 = null, loc2 = null;
    private Geometry geometry = null;
    private boolean cacheValid = false;

    public boolean isValid() {
        return loc1 != null && loc2 != null;
    }

    @Override public boolean isInBounds(Location loc) {
        if (!isValid()) {
            throw new IllegalStateException(
                "A call was made without the selection being complete!");
        }
        return loc.getWorld().equals(loc1.getWorld()) && isInBounds(loc.getX(), loc.getY(),
            loc.getZ());
    }

    @Override public boolean isInBounds(double x, double y, double z) {
        if (!isValid()) {
            throw new IllegalStateException(
                "A call was made without the selection being complete!");
        }
        return (getMinX() <= x) && (x <= getMaxX()) && (getMinY() <= y) && (y <= getMaxY()) && (
            getMinZ() <= z) && (z <= getMaxZ());
    }

    @Override public boolean intersects(BoundingArea box) {
        if (!box.getWorld().equals(getWorld())) {
            return false;
        }
        for (Point3I vertex : box.getBoundGeometry().getVertices()) {
            if (box.isInBounds(vertex.getX(), vertex.getY(), vertex.getZ())) {
                return true;
            }
        }
        return box.encapsulates(this);
    }

    @Override public Geometry getBoundGeometry() {
        if (geometry == null) {
            geometry = new CuboidGeometry();
        }
        return geometry;
    }


    @Override public int getMinX() {
        if (!isValid()) {
            throw new IllegalStateException(
                "A call was made without the selection being complete!");
        }
        if (!cacheValid) {
            generateMinMax();
        }
        return minX;
    }

    @Override public int getMaxX() {
        if (!isValid()) {
            throw new IllegalStateException(
                "A call was made without the selection being complete!");
        }
        if (!cacheValid) {
            generateMinMax();
        }
        return maxX;
    }

    @Override public int getMinY() {
        if (!isValid()) {
            throw new IllegalStateException(
                "A call was made without the selection being complete!");
        }
        if (!cacheValid) {
            generateMinMax();
        }
        return minY;
    }

    private void generateMinMax() {
        minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
        maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
        cacheValid = true;
    }

    @Override public int getMaxY() {
        if (!isValid()) {
            throw new IllegalStateException(
                "A call was made without the selection being complete!");
        }
        if (!cacheValid) {
            generateMinMax();
        }
        return maxY;
    }

    @Override public int getMinZ() {
        if (!isValid()) {
            throw new IllegalStateException(
                "A call was made without the selection being complete!");
        }
        if (!cacheValid) {
            generateMinMax();
        }
        return minZ;
    }

    @Override public int getMaxZ() {
        if (!isValid()) {
            throw new IllegalStateException(
                "A call was made without the selection being complete!");
        }
        if (!cacheValid) {
            generateMinMax();
        }
        return maxZ;
    }

    public Location getLoc1() {
        return loc1;
    }

    public void setLoc1(Location loc1) {
        this.loc1 = loc1;
        if (loc2 != null && !loc1.getWorld().equals(loc2.getWorld())) {
            loc2 = null;
        }
        cacheValid = false;
    }

    public Location getLoc2() {
        return loc2;
    }

    public void setLoc2(Location loc2) {
        this.loc2 = loc2;
        if (loc1 != null && !loc2.getWorld().equals(loc1.getWorld())) {
            loc1 = null;
        }
        cacheValid = false;
    }

    public World getWorld() {
        if (!isValid()) {
            throw new IllegalStateException(
                "A call was made without the selection being complete!");
        }
        return loc1.getWorld();
    }

    @Override public boolean encapsulates(BoundingArea other) {
        for (Point3I vertex : other.getBoundGeometry().getVertices()) {
            if (!isInBounds(vertex.getX(), vertex.getY(), vertex.getZ())) {
                return false;
            }
        }
        return true;
    }

    @Override public Map<Material, Integer> checkForBlocks(Map<Material, Integer> blocks) {
        return null; // TODO
    }

    @Override public int size2d() {
        return (getMaxX() - getMinX()) * (getMaxZ() - getMinZ());
    }

    @Override public int volume() {
        return size2d() * (getMaxY() - getMinY());
    }

    @Override public <T extends BoundingArea> T grow(Class<T> clazz, int size) {
        throw new UnsupportedOperationException("Selections can't be grown!");
    }

    @Override public BoundingArea flatten() {
        throw new UnsupportedOperationException("Selections can't be flattened!");
    }

    @Override public void initialize(JsonObject json) {
        // Not used
    }

    @Override public JsonObject save() {
        return null; // Not used
    }

    private class CuboidGeometry implements Geometry {

        private ArrayList<Point3I> vertices;
        private HashMap<Point3I, Collection<Line3I>> edges;
        private HashSet<Line3I> edgesUnique;
        private HashSet<Face> faces;
        private ArrayList<Rectangle> rectangle;

        private CuboidGeometry() {
            this.vertices = new ArrayList<>(8);
            vertices.add(new Point3I(getMinX(), getMinY(), getMinZ()));
            vertices.add(new Point3I(getMinX(), getMaxY(), getMinZ()));
            vertices.add(new Point3I(getMinX(), getMinY(), getMaxZ()));
            vertices.add(new Point3I(getMinX(), getMaxY(), getMaxZ()));
            vertices.add(new Point3I(getMaxX(), getMinY(), getMinZ()));
            vertices.add(new Point3I(getMaxX(), getMaxY(), getMinZ()));
            vertices.add(new Point3I(getMaxX(), getMinY(), getMaxZ()));
            vertices.add(new Point3I(getMaxX(), getMaxY(), getMaxZ()));
            Rectangle rect = new Rectangle(new Point3I(getMinX(), getMinY(), getMinZ()),
                new Point3I(getMaxX(), getMaxY(), getMaxZ()));
            this.rectangle = new ArrayList<>(1);
            this.rectangle.add(0, rect);
            this.edges = null;
            this.edgesUnique = null;
        }

        @Override public Collection<Point3I> getVertices() {
            return vertices;
        }

        @Override public Collection<Line3I> getEdges(Point3I vertex) {
            if (edges == null) {
                getAllEdges();
            }
            return edges.containsKey(vertex) ? edges.get(vertex) : Collections.<Line3I>emptyList();
        }

        @Override public Collection<Line3I> getAllEdges() {
            if (edgesUnique == null) {
                edges = new HashMap<>();
                edgesUnique = new LinkedHashSet<>();
                for (Point3I v : vertices) {
                    edges.put(v, new LinkedHashSet<>(3));
                }
                // TODO: something not O(n^2), although this is relatively small (n=8)
                for (Point3I v : vertices) {
                    for (Point3I v2 : vertices) {
                        if (v.equals(v2)) {
                            continue;
                        }
                        int shared = 0;
                        if (v.getX() == v2.getX()) {
                            shared++;
                        }
                        if (v.getY() == v2.getY()) {
                            shared++;
                        }
                        if (v.getZ() == v2.getZ()) {
                            shared++;
                        }
                        if (shared == 1) { // Single axis: is a line
                            Line3I line = new Line3I(v, v2);
                            edges.get(v).add(line);
                            edgesUnique.add(line);
                        }
                    }
                }
            }
            return edgesUnique;
        }

        @Override public Collection<Face> getFaces() {
            if (faces == null) {
                faces = new HashSet<>();
                if (edgesUnique == null) {
                    getAllEdges(); // Generate unique edges
                }
                // TODO: Also needs something not O(n^2), also relatively small (n=12)
                for (Line3I edge : edgesUnique) {
                    for (Line3I edge2 : edgesUnique) {
                        Point3I intersect = edge.getIntersection(edge2);
                        if (intersect != null) {
                            if ((intersect.equals(edge.getPoint1()) || intersect
                                .equals(edge.getPoint2())) && (intersect.equals(edge2.getPoint1())
                                || intersect.equals(edge2.getPoint2()))) {
                                int val =
                                    edge.getAxisOfTravel().asIntValue() + edge2.getAxisOfTravel()
                                        .asIntValue();
                                Axis remainder = Axis.fromIntValue(6 - val);
                                Axis axis1 = edge.getAxisOfTravel();
                                int value1 = axis1 == Axis.X ?
                                    edge2.getPoint1().getX() :
                                    axis1 == Axis.Z ?
                                        edge2.getPoint1().getZ() :
                                        edge2.getPoint1().getY();
                                Axis axis2 = edge2.getAxisOfTravel();
                                int value2 = axis2 == Axis.X ?
                                    edge.getPoint1().getX() :
                                    axis2 == Axis.Z ?
                                        edge.getPoint1().getZ() :
                                        edge.getPoint1().getY();
                                Face f = new Face(edge, edge2);
                                faces.add(f);
                            }
                        }
                    }
                }
            }
            return faces;
        }

        @Override public Collection<Rectangle> getBaseRectangles() {
            return rectangle;
        }
    }

}
