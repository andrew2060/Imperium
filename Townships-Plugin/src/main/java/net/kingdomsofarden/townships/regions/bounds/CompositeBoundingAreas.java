package net.kingdomsofarden.townships.regions.bounds;

import net.kingdomsofarden.townships.api.regions.bounds.BoundingArea;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.Collection;
import java.util.Map;

public class CompositeBoundingAreas implements BoundingArea {
    @Override
    public boolean isInBounds(Location loc) {
        return false;
    }

    @Override
    public boolean isInBounds(double x, double y, double z) {
        return false;
    }

    @Override
    public boolean intersects(BoundingArea box) {
        return false;
    }

    @Override
    public Collection<Integer[]> getVertices() {
        return null;
    }

    @Override
    public World getWorld() {
        return null;
    }

    @Override
    public boolean encapsulates(BoundingArea other) {
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
