package net.kingdomsofarden.townships.regions.bounds;

import com.google.gson.JsonObject;
import net.kingdomsofarden.townships.api.math.*;
import net.kingdomsofarden.townships.api.regions.bounds.BoundingArea;
import net.kingdomsofarden.townships.api.regions.bounds.CuboidBoundingBox;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public abstract class AbstractCuboidBoundingBox implements CuboidBoundingBox {
    
    protected int maxX;
    protected int maxY;
    protected int maxZ;
    protected int minX;
    protected int minY;
    protected int minZ;
    protected World world;
    protected BoundingArea flatten = null;
    protected HashMap<Integer, BoundingArea> growths;
    protected Geometry geometry;


    @Override public int getMinX() {
        return minX;
    }

    @Override public int getMaxX() {
        return maxX;
    }

    @Override public int getMinY() {
        return minY;
    }

    @Override public int getMaxY() {
        return maxY;
    }

    @Override public int getMinZ() {
        return minZ;
    }

    @Override public int getMaxZ() {
        return maxZ;
    }

    @Override public boolean isInBounds(Location loc) {
        return loc.getWorld().equals(world) && isInBounds(loc.getX(), loc.getY(), loc.getZ());
    }

    @Override public boolean isInBounds(double x, double y, double z) {
        return (minX <= x) && (x <= maxX) && (minY <= y) && (y <= maxY) && (minZ <= z) && (z
            <= maxZ);
    }

    @Override public boolean intersects(BoundingArea bounds) {
        if (!bounds.getWorld().equals(this.world)) {
            return false;
        }
        for (Point3I vertex : bounds.getBoundGeometry().getVertices()) {
            if (isInBounds(vertex.getX(), vertex.getY(), vertex.getZ())) {
                return true;
            }
        }
        return bounds.encapsulates(this);
    }

    @Override public Geometry getBoundGeometry() {
        return geometry;
    }

    @Override public World getWorld() {
        return world;
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
        return null; //TODO
    }

    @Override public int size2d() {
        return (maxZ - minZ) * (maxX - minX);
    }

    @Override public int volume() {
        return (maxZ - minZ) * (maxX - minX) * (maxY - minY);
    }

    @SuppressWarnings("unchecked") @Override public <T extends BoundingArea> T grow(Class<T> clazz, int size) {
        if (!CuboidBoundingBox.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("A " + clazz.getSimpleName() + " cannot be made from "
                + "a cuboid bounding box!");
        }
        if (!clazz.isInstance(this)) {
            try {
                Method m = clazz.getMethod("produceGrown", Integer.class);
                return (T) m.invoke(size);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                return null;
            }
        } else {
            if (growths.containsKey(size)) {
                return (T) growths.get(size);
            } else {
                T grown = produceGrown(size);
                growths.put(size, grown);
                return grown;
            }
        }
    }

    protected abstract <T extends BoundingArea> T produceGrown(int size);

    @Override public BoundingArea flatten() {
        return flatten == null ? (flatten = generateFlattened()) : flatten;
    }

    protected abstract BoundingArea generateFlattened();

    public void initialize(JsonObject json) {
        world = Bukkit.getWorld(UUID.fromString(json.get("world").getAsString()));
        JsonObject point1Info = json.get("point1").getAsJsonObject();
        JsonObject point2Info = json.get("point2").getAsJsonObject();
        Point3I point1 = Point3I.fromJsonObject(point1Info);
        Point3I point2 = Point3I.fromJsonObject(point2Info);
        minX = Math.min(point1.getX(), point2.getX());
        maxX = Math.max(point1.getX(), point2.getX());
        minY = Math.min(point1.getY(), point2.getY());
        maxY = Math.max(point1.getY(), point2.getY());
        minZ = Math.min(point1.getZ(), point2.getZ());
        maxZ = Math.max(point1.getZ(), point2.getZ());
        growths = new HashMap<>();
        geometry = new CuboidGeometry();
    }

    public JsonObject save() {
        JsonObject ret = new JsonObject();
        ret.addProperty("world", world.getUID().toString());
        ret.add("point1", new Point3I(minX, minY, minZ).asJsonObject());
        ret.add("point2", new Point3I(maxX, maxY, maxZ).asJsonObject());
        return ret;
    }

    protected class CuboidGeometry implements Geometry {
        private ArrayList<Point3I> vertices;
        private HashMap<Point3I, Collection<Segment3I>> edges;
        private HashSet<Segment3I> edgesUnique;
        private HashSet<Face> faces;
        private ArrayList<Rectangle> rectangle;

        protected CuboidGeometry() {
            this.vertices = new ArrayList<>(8);
            this.vertices.add(new Point3I(minX, minY, minZ));
            this.vertices.add(new Point3I(minX, maxY, minZ));
            this.vertices.add(new Point3I(minX, minY, maxZ));
            this.vertices.add(new Point3I(minX, maxY, maxZ));
            this.vertices.add(new Point3I(maxX, minY, minZ));
            this.vertices.add(new Point3I(maxX, maxY, minZ));
            this.vertices.add(new Point3I(maxX, minY, maxZ));
            this.vertices.add(new Point3I(maxX, maxY, maxZ));
            Rectangle rect = new Rectangle(new Point3I(minX, minY, minZ), new Point3I(maxX, minY,
                maxZ));
            this.rectangle = new ArrayList<>(1);
            this.rectangle.add(0, rect);
            this.edges = null;
            this.edgesUnique = null;
        }

        @Override public Collection<Point3I> getVertices() {
            return vertices;
        }

        @Override public Collection<Segment3I> getEdges(Point3I vertex) {
            if (edges == null) {
                getAllEdges();
            }
            return edges.containsKey(vertex) ? edges.get(vertex) : Collections.<Segment3I>emptyList();
        }

        @Override public Collection<Segment3I> getAllEdges() {
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
                            Segment3I line = new Segment3I(v, v2);
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
                for (Segment3I edge : edgesUnique) {
                    for (Segment3I edge2 : edgesUnique) {
                        Point3I intersect = edge.getIntersection(edge2);
                        if (intersect != null) {
                            if ((intersect.equals(edge.getPoint1()) || intersect.equals(edge
                                .getPoint2())) && (intersect.equals(edge2.getPoint1()) ||
                                intersect.equals(edge2.getPoint2()))) {
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
