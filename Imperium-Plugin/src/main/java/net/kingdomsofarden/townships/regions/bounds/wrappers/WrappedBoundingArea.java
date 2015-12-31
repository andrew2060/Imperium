package net.kingdomsofarden.townships.regions.bounds.wrappers;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.Region;
import net.kingdomsofarden.townships.api.regions.bounds.BoundingArea;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public abstract class WrappedBoundingArea implements BoundingArea {

    protected final Region bounds;
    protected final World world;
    protected final ArrayList<Vector> vertices;

    public WrappedBoundingArea(Region region) {
        this.bounds = region;
        this.world = Bukkit.getWorld(region.getWorld().getName());
        this.vertices = new ArrayList<>();
        computeVertices();
    }

    @Override public Map<Material, Integer> checkForBlocks(Map<Material, Integer> blocks) {
        if (blocks.isEmpty()) {
            return blocks;
        }
        for (BlockVector blockCoord : bounds) {
            Block b = world
                .getBlockAt(blockCoord.getBlockX(), blockCoord.getBlockY(), blockCoord.getBlockZ());
            blocks.computeIfPresent(b.getType(), (material, amount) -> {
                amount--;
                return amount == 0 ? null : amount;
            });
            if (blocks.isEmpty()) {
                break;
            }
        }
        return blocks;
    }

    @Override public int area() {
        return bounds.getArea();
    }

    @Override public int volume() {
        return bounds.getArea() * bounds.getHeight();
    }


    @Override public boolean isInBounds(Location loc) {
        return loc.getWorld().getUID().equals(world.getUID()) && isInBounds(loc.getX(), loc.getY(),
            loc.getZ());
    }

    @Override public boolean isInBounds(double x, double y, double z) {
        return bounds.contains(new Vector(x,y,z));
    }

    @Override public boolean intersects(BoundingArea box) {
        if (!box.getWorld().getUID().equals(world.getUID())) {
            return false;
        }
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
        if (!other.getWorld().getUID().equals(world.getUID())) {
            return false;
        }
        for (Vector v : other.getVertices()) {
            if (!isInBounds(v.getX(), v.getY(), v.getZ())) {
                return false;
            }
        }
        return true;
    }

    @Override public Collection<Vector> getVertices() {
        return vertices;
    }

    public abstract void computeVertices();
}
