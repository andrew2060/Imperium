package net.kingdomsofarden.townships.regions.bounds;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import net.kingdomsofarden.townships.api.regions.FunctionalRegion;
import net.kingdomsofarden.townships.api.regions.bounds.BoundingArea;
import net.kingdomsofarden.townships.regions.bounds.wrappers.WrappedBoundingArea;

import java.awt.*;
import java.awt.geom.Area;
import java.util.List;

public class PolygonalBoundingArea extends WrappedBoundingArea {

    public PolygonalBoundingArea(Polygonal2DRegion region, FunctionalRegion tRegion) {
        super(region, tRegion);
    }

    @Override public <T extends BoundingArea> T grow(Class<T> clazz, int size) {
        Vector center = bounds.getCenter();
        int minY = ((Polygonal2DRegion) bounds).getMinimumY() - size;
        int maxY = ((Polygonal2DRegion) bounds).getMaximumY() + size;
        Polygonal2DRegion expanded = new Polygonal2DRegion(bounds.getWorld());
        ((Polygonal2DRegion) bounds).getPoints().forEach(outer -> {
            // Discounting y is not mathematically accurate, but done for usability's sake
            Vector extend =
                new Vector(outer.getX() - center.getX(), 0, outer.getZ() - center.getZ())
                    .normalize();
            Vector newOuter =
                outer.add(extend.multiply(size).toVector2D()).toVector().toBlockVector();
            expanded.addPoint(newOuter);
        });
        expanded.setMinimumY(minY);
        expanded.setMaximumY(maxY);
        return (T) new PolygonalBoundingArea(expanded, tRegion);
    }

    @Override public JsonObject toJson() {
        JsonObject ret = new JsonObject();
        ret.addProperty("type", "POLY");
        JsonArray pointsArr = new JsonArray();
        ((Polygonal2DRegion) bounds).getPoints()
            .forEach(point -> pointsArr.add(VECTORSERIALIZER.serialize2d(point, true)));
        ret.add("points", pointsArr);
        ret.addProperty("minY", ((Polygonal2DRegion) bounds).getMinimumY());
        ret.addProperty("maxY", ((Polygonal2DRegion) bounds).getMaximumY());
        return ret;
    }

    @Override public Area asAWTArea() {
        // Derived from WorldGuard's Implementation TODO: add to licenses
        List<BlockVector2D> points = ((Polygonal2DRegion) bounds).getPoints();
        int numPoints = points.size();
        int[] xCoords = new int[numPoints];
        int[] yCoords = new int[numPoints];
        int i = 0;
        for (BlockVector2D point : points) {
            xCoords[i] = point.getBlockX();
            yCoords[i] = point.getBlockZ();
            i++;
        }
        Polygon polygon = new Polygon(xCoords, yCoords, numPoints);
        return new Area(polygon);
    }


}
