package net.kingdomsofarden.townships.regions.bounds;

import net.kingdomsofarden.townships.api.regions.bounds.BoundingArea;
import net.kingdomsofarden.townships.api.regions.bounds.CuboidBoundingBox;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.Collection;
import java.util.Map;

/**
 * A constructed bounding area, not for direct use
 */
public class ComplexCuboidBoundingArea implements BoundingArea {

    private CuboidBoundingBox main;
    private CompositeBoundingAreas deductions;

    public void add(CuboidBoundingBox box) {
        if (main == null) {
            main = box; // Do no more work
            deductions = new CompositeBoundingAreas();
        } else {
            // Determine new minimum spanning
            if (!main.encapsulates(box)) { // If our box already encapsulates we don't need to calculate a new minimum spanning
                if (box.encapsulates(main)) {
                    deductions = new CompositeBoundingAreas();
                    main = box;
                    return;
                } else {
                    int minX = Math.min(box.getMinX(), main.getMinX());
                    int minY = Math.min(box.getMinY(), main.getMinY());
                    int minZ = Math.min(box.getMinZ(), main.getMinZ());
                    int maxX = Math.max(box.getMaxX(), main.getMaxX());
                    int maxY = Math.max(box.getMaxY(), main.getMaxY());
                    int maxZ = Math.max(box.getMaxZ(), main.getMaxZ());
                    main = new DynamicCuboidBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
                }
            }

        }
    }

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
