package net.kingdomsofarden.townships.regions.bounds;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.kingdomsofarden.townships.api.regions.bounds.BoundingArea;

public class AxisAlignedBoundingBox extends AbstractCuboidBoundingBox {

    @Override protected <T extends BoundingArea> T produceGrown(int size) {
        T ret = (T) new AxisAlignedBoundingBox();
        JsonObject init = new JsonObject();
        init.add("world", new JsonPrimitive(world.getUID().toString()));
        JsonObject point1 = new JsonObject();
        point1.add("x", new JsonPrimitive(minX - size));
        point1.add("y", new JsonPrimitive(minY - size));
        point1.add("z", new JsonPrimitive(minZ - size));
        JsonObject point2 = new JsonObject();
        point2.add("x", new JsonPrimitive(maxX + size));
        point2.add("y", new JsonPrimitive(maxY + size));
        point2.add("z", new JsonPrimitive(maxZ + size));
        init.add("point1", point1);
        init.add("point2", point2);
        ret.initialize(init);
        return ret;
    }

    @Override protected BoundingArea generateFlattened() {
        AxisAlignedBoundingBox ret =  new AxisAlignedBoundingBox();
        JsonObject init = new JsonObject();
        init.add("world", new JsonPrimitive(world.getUID().toString()));
        JsonObject point1 = new JsonObject();
        point1.add("x", new JsonPrimitive(minX));
        point1.add("y", new JsonPrimitive(0));
        point1.add("z", new JsonPrimitive(minZ));
        JsonObject point2 = new JsonObject();
        point2.add("x", new JsonPrimitive(maxX));
        point2.add("y", new JsonPrimitive(0));
        point2.add("z", new JsonPrimitive(maxZ));
        init.add("point1", point1);
        init.add("point2", point2);
        ret.initialize(init);
        return ret;
    }
}
