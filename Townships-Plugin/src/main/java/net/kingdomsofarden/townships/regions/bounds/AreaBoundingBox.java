package net.kingdomsofarden.townships.regions.bounds;

import net.kingdomsofarden.townships.api.math.Geometry;
import net.kingdomsofarden.townships.api.math.Point3I;
import net.kingdomsofarden.townships.api.math.RectangularGeometry;
import net.kingdomsofarden.townships.api.regions.bounds.BoundingArea;
import net.kingdomsofarden.townships.api.regions.bounds.CuboidBoundingBox;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class AreaBoundingBox extends AbstractCuboidBoundingBox {

    public AreaBoundingBox(World world, int minX, int maxX, int minZ, int maxZ) {
        super(world, new Point3I(minX, Integer.MIN_VALUE, minZ), new Point3I(maxX, Integer
            .MAX_VALUE, maxZ));
    }

}
