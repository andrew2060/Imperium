package net.kingdomsofarden.townships.regions.bounds;

import com.google.gson.JsonObject;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.Region;
import net.kingdomsofarden.townships.api.regions.bounds.BoundingArea;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

public class CompositeBoundingArea implements BoundingArea {

    private final World world;
    private Collection<BlockVector> blockVectors;
    private Collection<BlockVector2D> flattened;

    private Collection<BoundingArea> regions;

    public CompositeBoundingArea(World world) {
        this.blockVectors = new HashSet<>();
        this.flattened = new HashSet<>();
        this.world = world;
        this.regions = new HashSet<>();
    }

    public void add(BoundingArea bounds) {
        if (!bounds.getWorld().equals(world)) {
            throw new IllegalArgumentException("Worlds don't match on bounding area addition");
        }
        regions.add(bounds);
        for (BlockVector b : bounds.getBacking()) {
            blockVectors.add(b);
            flattened.add(b.toVector2D().toBlockVector2D());
        }
    }


    @Override public boolean isInBounds(Location loc) {
        return world.equals(loc.getWorld()) && isInBounds(loc.getX(), loc.getY(), loc.getZ());
    }

    @Override public boolean isInBounds(double x, double y, double z) {
        return false;
    }

    @Override public boolean intersects(BoundingArea box) {
        return false;
    }

    @Override public World getWorld() {
        return world;
    }

    @Override public boolean encapsulates(BoundingArea other) {
        return false;
    }

    @Override public Map<Material, Integer> checkForBlocks(Map<Material, Integer> blocks) {
        if (blocks.isEmpty()) {
            return blocks;
        }
        for (BlockVector blockCoord : blockVectors) {
            Block b = world
                .getBlockAt(blockCoord.getBlockX(), blockCoord.getBlockY(), blockCoord.getBlockZ());
            blocks
                .computeIfPresent(b.getType(), (material, amount) -> --amount == 0 ? null : amount);
            if (blocks.isEmpty()) {
                break;
            }
        }
        return blocks;
    }

    @Override public int area() {
        return flattened.size();
    }

    @Override public int volume() {
        return blockVectors.size();
    }

    @Override public Collection<Vector> getVertices() {
        return null;
    }

    @Override public <T extends BoundingArea> T grow(Class<T> clazz, int size) {
        return null;
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
