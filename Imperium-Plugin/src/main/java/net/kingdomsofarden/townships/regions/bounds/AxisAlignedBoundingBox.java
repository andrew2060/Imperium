package net.kingdomsofarden.townships.regions.bounds;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.kingdomsofarden.townships.api.math.Point3I;
import net.kingdomsofarden.townships.api.regions.bounds.BoundingArea;

public class AxisAlignedBoundingBox extends AbstractCuboidBoundingBox {

    @SuppressWarnings("unchecked") @Override protected <T extends BoundingArea> T produceGrown(int size) {
        T ret = (T) new AxisAlignedBoundingBox();
        JsonObject init = new JsonObject();
        init.add("world", new JsonPrimitive(world.getUID().toString()));
        init.add("point1", new Point3I(minX - size, minY - size, minZ - size).asJsonObject());
        init.add("point2", new Point3I(maxX + size, maxY + size, maxZ + size).asJsonObject());
        ret.initialize(init);
        return ret;
    }

    @Override protected BoundingArea generateFlattened() {
        AxisAlignedBoundingBox ret =  new AxisAlignedBoundingBox();
        JsonObject init = new JsonObject();
        init.addProperty("world", world.getUID().toString());
        init.add("point1", new Point3I(minX, 0, minZ).asJsonObject());
        init.add("point2", new Point3I(maxX, 0, maxZ).asJsonObject());
        ret.initialize(init);
        return ret;
    }
}
