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

import java.awt.geom.Area;
import java.util.Map;

public abstract class WrappedBoundingArea implements BoundingArea {

    protected net.kingdomsofarden.townships.api.regions.Region tRegion;
    protected Region bounds;
    protected World world;

    public WrappedBoundingArea(Region region,
        net.kingdomsofarden.townships.api.regions.Region tRegion) {
        this.bounds = region;
        this.world = Bukkit.getWorld(region.getWorld().getName());
        this.tRegion = tRegion;
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

    @Override public Region getBacking() {
        return bounds;
    }


    @Override public boolean isInBounds(Location loc) {
        return loc.getWorld().getUID().equals(world.getUID()) && isInBounds(loc.getX(), loc.getY(),
            loc.getZ());
    }

    @Override public boolean isInBounds(double x, double y, double z) {
        return bounds.contains(new Vector(x, y, z));
    }

    @Override public boolean intersects(BoundingArea other) {
        // Simplify to a box for some quick filtering
        // - Check one side
        BlockVector oMax = other.getBacking().getMaximumPoint().toBlockVector();
        BlockVector min = getBacking().getMinimumPoint().toBlockVector();
        if (oMax.getBlockX() < min.getBlockX() || oMax.getBlockY() < min.getBlockY()
            || oMax.getBlockZ() < min.getBlockZ()) {
            return false;
        }
        // - And the other
        BlockVector oMin = other.getBacking().getMinimumPoint().toBlockVector();
        BlockVector max = getBacking().getMaximumPoint().toBlockVector();
        if (oMin.getBlockX() > max.getBlockX() || oMin.getBlockY() > max.getBlockY()
            || oMin.getBlockZ() > max.getBlockZ()) {
            return false;
        }
        // Box's can intersect, do a more thorough check
        Area oArea = other.asAWTArea();
        oArea.intersect(asAWTArea());
        return !oArea.isEmpty();
    }

    @Override public World getWorld() {
        return world;
    }

    @Override public boolean encapsulates(BoundingArea other) {
        if (!intersects(other)) {
            return false;
        }
        Area awtThis = asAWTArea();
        Area awtOther = other.asAWTArea();
        awtOther.subtract(awtThis);
        return awtOther.isEmpty() && other.getBacking().getMinimumPoint().getBlockY() > bounds
            .getMinimumPoint().getBlockY()
            && other.getBacking().getMaximumPoint().getBlockY() < bounds.getMaximumPoint()
            .getBlockY();
    }

    public net.kingdomsofarden.townships.api.regions.Region getRegion() {
        return tRegion;
    }

}
