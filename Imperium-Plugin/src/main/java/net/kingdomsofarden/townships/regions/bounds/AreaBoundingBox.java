package net.kingdomsofarden.townships.regions.bounds;

import com.google.gson.JsonObject;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.Region;
import net.kingdomsofarden.townships.api.regions.bounds.BoundingArea;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class AreaBoundingBox implements BoundingArea {

    protected int maxX;
    protected int maxY;
    protected int maxZ;
    protected int minX;
    protected int minY;
    protected int minZ;
    protected World world;
    private ArrayList<Vector> vertices;

    public AreaBoundingBox(World world, int minX, int maxX, int minZ, int maxZ) {
        this.world = world;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = Integer.MIN_VALUE;
        this.maxY = Integer.MAX_VALUE;
        this.minZ = minZ;
        this.maxZ = maxZ;
        this.vertices = new ArrayList<>(8);
        this.vertices.add(new Vector(minX, minY, minZ));
        this.vertices.add(new Vector(minX, maxY, minZ));
        this.vertices.add(new Vector(minX, minY, maxZ));
        this.vertices.add(new Vector(minX, maxY, maxZ));
        this.vertices.add(new Vector(maxX, minY, minZ));
        this.vertices.add(new Vector(maxX, maxY, minZ));
        this.vertices.add(new Vector(maxX, minY, maxZ));
        this.vertices.add(new Vector(maxX, maxY, maxZ));
    }

    @Override public boolean isInBounds(Location loc) {
        return loc.getWorld().getUID().equals(world.getUID()) && isInBounds(loc.getX(), loc.getY(),
            loc.getZ());
    }

    @Override public boolean isInBounds(double x, double y, double z) {
        return (minX <= x) && (x <= maxX) && (minY <= y) && (y <= maxY) && (minZ <= z) && (z
            <= maxZ);
    }

    @Override public boolean intersects(BoundingArea box) {
        if (!box.getWorld().equals(this.world)) {
            return false;
        }
        for (Vector vertex : box.getVertices()) {
            if (isInBounds(vertex.getX(), vertex.getY(), vertex.getZ())) {
                return true;
            }
        }
        return box.encapsulates(this);
    }

    @Override public World getWorld() {
        return world;
    }

    @Override public boolean encapsulates(BoundingArea other) {
        for (Vector vertex : other.getVertices()) {
            if (!isInBounds(vertex.getX(), vertex.getY(), vertex.getZ())) {
                return false;
            }
        }
        return true;
    }

    @Override public Map<Material, Integer> checkForBlocks(Map<Material, Integer> blocks) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override public int area() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override public int volume() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override public Collection<Vector> getVertices() {
        return vertices;
    }

    @Override public <T extends BoundingArea> T grow(Class<T> clazz, int size) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override public void initialize(JsonObject json) {

    }

    @Override public JsonObject save() {
        return null;
    }

    @Override public Region getBacking() {
        return null;
    }
}
