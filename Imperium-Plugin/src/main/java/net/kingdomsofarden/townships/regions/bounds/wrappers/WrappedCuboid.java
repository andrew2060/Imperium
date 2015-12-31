package net.kingdomsofarden.townships.regions.bounds.wrappers;

import com.google.gson.JsonObject;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import net.kingdomsofarden.townships.api.regions.bounds.BoundingArea;
import net.kingdomsofarden.townships.api.regions.bounds.CuboidBoundingBox;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class WrappedCuboid implements CuboidBoundingBox {

    private final CuboidRegion cube;
    private final Vector min;
    private final Vector max;
    private final World world;
    private final List<Vector> vertices;

    @SuppressWarnings("ConstantConditions") public WrappedCuboid(CuboidRegion cube) {
        this.cube = cube;
        this.min = cube.getMinimumPoint();
        this.max = cube.getMaximumPoint();
        this.world = Bukkit.getWorld(cube.getWorld().getName());
        this.vertices = new ArrayList<>(8);
        this.vertices.add(new Vector(getMinX(), getMinY(), getMinZ()));
        this.vertices.add(new Vector(getMinX(), getMaxY(), getMinZ()));
        this.vertices.add(new Vector(getMinX(), getMinY(), getMaxZ()));
        this.vertices.add(new Vector(getMinX(), getMaxY(), getMaxZ()));
        this.vertices.add(new Vector(getMaxX(), getMinY(), getMinZ()));
        this.vertices.add(new Vector(getMaxX(), getMaxY(), getMinZ()));
        this.vertices.add(new Vector(getMaxX(), getMinY(), getMaxZ()));
        this.vertices.add(new Vector(getMaxX(), getMaxY(), getMaxZ()));
    }

    @Override public int getMinX() {
        return min.getBlockX();
    }

    @Override public int getMaxX() {
        return max.getBlockX();
    }

    @Override public int getMinY() {
        return min.getBlockY();
    }

    @Override public int getMaxY() {
        return max.getBlockY();
    }

    @Override public int getMinZ() {
        return min.getBlockZ();
    }

    @Override public int getMaxZ() {
        return max.getBlockZ();
    }

    @Override public CuboidRegion getBackingBounds() {
        return cube;
    }

    @Override public boolean isInBounds(Location loc) {
        return loc.getWorld().getUID().equals(world.getUID()) && isInBounds(loc.getX(), loc.getY(),
            loc.getZ());
    }

    @Override public boolean isInBounds(double x, double y, double z) {
        return x >= (double) min.getBlockX() && x <= (double) max.getBlockX() && y >= (double) min
            .getBlockY() && y <= (double) max.getBlockY() && z >= (double) min.getBlockZ()
            && z <= (double) max.getBlockZ();
    }

    @Override public boolean intersects(BoundingArea box) {
        for (Vector v : box.getVertices()) {
            if (isInBounds(v.getX(), v.getY(), v.getZ())) {
                return true;
            }
        }
        return box.encapsulates(this);
    }

    @Override public World getWorld() {
        return world;
    }

    @Override public boolean encapsulates(BoundingArea other) {
        for (Vector v : other.getVertices()) {
            if (!isInBounds(v.getX(), v.getY(), v.getZ())) {
                return false;
            }
        }
        return true;
    }

    @Override public Map<Material, Integer> checkForBlocks(Map<Material, Integer> blocks) {
        return null;
    }

    @Override public int area() {
        return cube.getArea();
    }

    @Override public int volume() {
        return cube.getArea() * cube.getHeight();
    }

    @Override public Collection<Vector> getVertices() {
        return vertices;
    }

    @Override public <T extends BoundingArea> T grow(Class<T> clazz, int size) {
        Vector newMin = new Vector(min.getX() - size, min.getY() - size, min.getZ() - size);
        Vector newMax = new Vector(max.getX() + size, max.getY() + size, max.getZ() + size);
        return (T) new WrappedCuboid(new CuboidRegion(newMin, newMax));
    }

    @Override public void initialize(JsonObject json) {

    }

    @Override public JsonObject save() {
        return null;
    }



}
