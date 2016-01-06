package net.kingdomsofarden.townships.regions.bounds;

import com.google.gson.JsonObject;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.Region;
import net.kingdomsofarden.townships.api.regions.bounds.BoundingArea;
import net.kingdomsofarden.townships.regions.collections.AxisBoundCollection;
import net.kingdomsofarden.townships.regions.collections.RegionBoundCollection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.awt.geom.Area;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;

public class CompositeBoundingArea implements BoundingArea {

    private final World world;
    private final net.kingdomsofarden.townships.api.regions.Region region;
    private Collection<BlockVector> blockVectors;
    private Collection<BlockVector2D> flattened;

    private RegionBoundCollection regions;

    public CompositeBoundingArea(World world,
        net.kingdomsofarden.townships.api.regions.Region region) {
        this.blockVectors = new HashSet<>();
        this.flattened = new HashSet<>();
        this.world = world;
        this.regions = new AxisBoundCollection(world, true);
        this.region = region;
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

    public void remove(BoundingArea bounds) {
        if (!bounds.getWorld().equals(world)) {
            throw new IllegalArgumentException("Worlds don't match on bounding area removal");
        }
        regions.remove(bounds.getRegion());

        for (BlockVector v : bounds.getBacking()) {
            if (regions.getBoundingRegions(v.getBlockX(), v.getBlockY(), v.getBlockZ()).isEmpty()) {
                blockVectors.remove(v);
                BlockVector2D fV = v.toVector2D().toBlockVector2D();
                if (regions.getFlattenedBoundingRegions(fV.getBlockX(), fV.getBlockZ()).isEmpty()) {
                    flattened.remove(fV);
                }
            }
        }
    }


    @Override public boolean isInBounds(Location loc) {
        return world.equals(loc.getWorld()) && isInBounds(loc.getX(), loc.getY(), loc.getZ());
    }

    @Override public boolean isInBounds(double x, double y, double z) {
        return blockVectors.contains(new Vector(x,y,z).toBlockVector());
    }

    @Override public boolean intersects(BoundingArea bounds) {
        TreeSet<net.kingdomsofarden.townships.api.regions.Region> regionColl = new TreeSet<>();
        regions.getIntersectingRegions(bounds, regionColl);
        return !regionColl.isEmpty();
    }

    @Override public World getWorld() {
        return world;
    }

    @Override public boolean encapsulates(BoundingArea other) {
        TreeSet<net.kingdomsofarden.townships.api.regions.Region> regionColl = new TreeSet<>();
        regions.getIntersectingRegions(other, regionColl);

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

    @Override public <T extends BoundingArea> T grow(Class<T> clazz, int size) {
        CompositeBoundingArea ret = new CompositeBoundingArea(world, region);
        for (BoundingArea b : regions.getContainedBounds()) {
            ret.add(b.grow(BoundingArea.class, size));
        }
        return (T) ret;
    }

    @Override public net.kingdomsofarden.townships.api.regions.Region getRegion() {
        return region;
    }

    @Override public void initialize(JsonObject json) {

    }

    @Override public JsonObject save() {
        return null;
    }

    @Override public Region getBacking() {
        return null;
    }

    @Override public Area asAWTArea() {
        return null;
    }

}
