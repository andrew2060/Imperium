package net.kingdomsofarden.townships.regions.bounds;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionIntersection;
import net.kingdomsofarden.townships.api.regions.FunctionalRegion;
import net.kingdomsofarden.townships.api.regions.bounds.BoundingArea;
import net.kingdomsofarden.townships.regions.collections.AxisBoundCollection;
import net.kingdomsofarden.townships.regions.collections.RegionBoundCollection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.awt.geom.Area;
import java.util.*;

public class CompositeBoundingArea implements BoundingArea {

    private final World world;
    private final FunctionalRegion region;
    private final double zocMultiplier;
    private Collection<BlockVector> blockVectors;
    private Collection<BlockVector2D> flattened;

    private RegionBoundCollection regions;

    public CompositeBoundingArea(World world, FunctionalRegion region, double zocMultiplier) {
        this.blockVectors = new HashSet<>();
        this.flattened = new HashSet<>();
        this.world = world;
        this.regions = new AxisBoundCollection(world, true);
        this.region = region;
        this.zocMultiplier = zocMultiplier;
    }

    public void add(FunctionalRegion region) {
        if (!region.getBounds().getWorld().equals(world)) {
            throw new IllegalArgumentException("Cannot add non-equivalent world regions to the "
                + "same composite region");
        }
        BoundingArea add = region.getBounds()
            .grow(BoundingArea.class, (int) Math.ceil(region.getZOC() * zocMultiplier));
        regions.add(add);
        for (BlockVector b : add.getBacking()) {
            blockVectors.add(b);
            flattened.add(b.toVector2D().toBlockVector2D());
        }
    }

    public void remove(FunctionalRegion region) {
        if (!region.getBounds().getWorld().equals(world)) {
            throw new IllegalArgumentException("Cannot add non-equivalent world regions to the "
                + "same composite region");
        }
        regions.remove(region);

        for (BlockVector v : region.getBounds().grow(BoundingArea.class, (int) Math.ceil(region
            .getZOC() * zocMultiplier)).getBacking()) {
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
        return blockVectors.contains(new Vector(x, y, z).toBlockVector());
    }

    @Override public boolean intersects(BoundingArea bounds) {
        TreeSet<FunctionalRegion> regionColl = new TreeSet<>();
        regions.getIntersectingRegions(bounds, regionColl);
        return !regionColl.isEmpty();
    }

    @Override public World getWorld() {
        return world;
    }

    @Override public boolean encapsulates(BoundingArea other) {
        Set<BlockVector> blocks = new HashSet<>(blockVectors);
        for (BoundingArea b : regions.getIntersectingBounds(other)) {
            b.getBacking().forEach(blocks::remove);
        }
        return blocks.isEmpty();
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
        CompositeBoundingArea ret = new CompositeBoundingArea(world, region, zocMultiplier * size);
        for (BoundingArea b : regions.getContainedBounds()) {
            ret.add(b.getRegion());
        }
        return (T) ret;
    }

    @Override public FunctionalRegion getRegion() {
        return region;
    }

    @Override public JsonObject toJson() {
        JsonObject ret = new JsonObject();
        ret.addProperty("type", "COMPOSITE");
        JsonArray regionArr = new JsonArray();
        regions.getContainedBounds()
            .forEach(r -> regionArr.add(new JsonPrimitive(r.getRegion().getUid().toString())));
        ret.add("regions", regionArr);
        ret.addProperty("multiplier", zocMultiplier);
        return ret;
    }

    @Override public Region getBacking() {
        List<Region> ret = new LinkedList<>();
        regions.getContainedBounds().forEach(b -> ret.add(b.getBacking()));
        return new RegionIntersection(ret);
    }

    @Override public Area asAWTArea() {
        Area area = new Area();
        for (BoundingArea bounds : regions.getContainedBounds()) {
            area.add(bounds.asAWTArea());
        }
        return area;
    }

}
