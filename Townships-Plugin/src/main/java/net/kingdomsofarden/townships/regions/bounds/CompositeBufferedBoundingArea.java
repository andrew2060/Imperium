package net.kingdomsofarden.townships.regions.bounds;

import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.regions.bounds.BoundingArea;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

public class CompositeBufferedBoundingArea implements BoundingArea {

    private BoundingArea[] bounds; // Relatively static expected size
    private World world;
    private Collection<Region> regions;

    public void reconstruct() {

    }

    @Override
    public boolean isInBounds(Location loc) {
        for (BoundingArea sub : bounds) {
            if (sub.isInBounds(loc)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isInBounds(double x, double y, double z) {
        for (BoundingArea sub : bounds) {
            if (sub.isInBounds(x, y, z)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean intersects(BoundingArea box) {
        for (BoundingArea sub : bounds) {
            if (sub.intersects(box)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Collection<Integer[]> getVertices() {
        LinkedList<Integer[]> ret = new LinkedList<Integer[]>();
        for (BoundingArea box : bounds) {
            ret.addAll(box.getVertices());
        }
        return ret;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public boolean encapsulates(BoundingArea other) {
        for (BoundingArea box : bounds) {
            if (box.encapsulates(other)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Map<Material, Integer> checkForBlocks(Map<Material, Integer> blocks) {
        return null;
    }

    @Override
    public int size2d() {
        return 0;
    }

    @Override
    public int volume() {
        return 0;
    }
}
