package net.kingdomsofarden.townships.regions.bounds;

import net.kingdomsofarden.townships.api.regions.bounds.BoundingBox;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Collection;
import java.util.LinkedList;

public class CompositeBoundingBox implements BoundingBox {

    private BoundingBox[] bounds; // Relatively static expected size
    private World world;

    @Override
    public boolean isInBounds(Location loc) {
        for (BoundingBox sub : bounds) {
            if (sub.isInBounds(loc)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isInBounds(double x, double y, double z) {
        for (BoundingBox sub : bounds) {
            if (sub.isInBounds(x, y, z)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean intersects(BoundingBox box) {
        for (BoundingBox sub : bounds) {
            if (sub.intersects(box)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Collection<Integer[]> getVertices() {
        LinkedList<Integer[]> ret = new LinkedList<Integer[]>();
        for (BoundingBox box : bounds) {
            ret.addAll(box.getVertices());
        }
        return ret;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public boolean encapsulates(BoundingBox other) {
        for (BoundingBox box : bounds) {
            if (box.encapsulates(other)) {
                return true;
            }
        }
        return false;
    }
}
