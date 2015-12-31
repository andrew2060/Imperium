package net.kingdomsofarden.townships.regions.bounds;

import com.google.gson.JsonObject;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import net.kingdomsofarden.townships.api.regions.bounds.BoundingArea;
import net.kingdomsofarden.townships.regions.bounds.wrappers.WrappedBoundingArea;

public class PolygonalBoundingArea extends WrappedBoundingArea {

    public PolygonalBoundingArea(Polygonal2DRegion region) {
        super(region);
    }

    @Override public <T extends BoundingArea> T grow(Class<T> clazz, int size) {
        Vector center = bounds.getCenter();
        int minY = ((Polygonal2DRegion)bounds).getMinimumY() - size;
        int maxY = ((Polygonal2DRegion)bounds).getMaximumY() + size;
        Polygonal2DRegion expanded = new Polygonal2DRegion(bounds.getWorld());
        for (int i = 0; i < vertices.size(); i += 2) {
            Vector outer = vertices.get(i);
            // Discounting y is not mathematically accurate, but done for usability's sake
            Vector extend =
                new Vector(outer.getX() - center.getX(), 0, outer.getZ() - center.getZ())
                    .normalize();
            Vector newOuter = outer.add(extend.multiply(size)).toBlockVector();
            expanded.addPoint(newOuter);
        }
        expanded.setMinimumY(minY);
        expanded.setMaximumY(maxY);
        return (T) new PolygonalBoundingArea(expanded);
    }

    @Override public void initialize(JsonObject json) {

    }

    @Override public JsonObject save() {
        return null;
    }

    @Override public void computeVertices() {
        ((Polygonal2DRegion)bounds).getPoints().stream().forEach(p -> {
            vertices.add(new Vector(p.getX(), ((Polygonal2DRegion)bounds).getMinimumY(), p.getZ()));
            vertices.add(new Vector(p.getX(), ((Polygonal2DRegion)bounds).getMaximumY(), p.getZ()));
        });
    }
}
