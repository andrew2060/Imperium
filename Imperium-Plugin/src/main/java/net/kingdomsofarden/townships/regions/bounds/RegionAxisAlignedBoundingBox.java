package net.kingdomsofarden.townships.regions.bounds;

import com.google.gson.JsonObject;
import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.api.math.Point3I;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.regions.bounds.BoundingArea;
import net.kingdomsofarden.townships.api.regions.bounds.RegionBoundingArea;

import java.util.UUID;

public class RegionAxisAlignedBoundingBox extends AxisAlignedBoundingBox
    implements RegionBoundingArea {
    private Region region;

    @Override public Region getRegion() {
        return region;
    }

    @Override public void initialize(JsonObject json) {
        super.initialize(json);
        region = Townships.getRegions().get(UUID.fromString(json.get("region").getAsString()))
            .orNull();
    }

    @Override public JsonObject save() {
        JsonObject obj = super.save();
        obj.addProperty("region", region.getUid().toString());
        return obj;
    }

    @SuppressWarnings("unchecked") @Override protected <T extends BoundingArea> T produceGrown(int size) {
        T ret = (T) new RegionAxisAlignedBoundingBox();
        JsonObject init = new JsonObject();
        init.addProperty("world", world.getUID().toString());
        init.add("point1", new Point3I(minX - size, minY - size, minZ - size).asJsonObject());
        init.add("point2", new Point3I(maxX + size, maxY + size, maxZ + size).asJsonObject());
        init.addProperty("region", region.getUid().toString());
        ret.initialize(init);
        return ret;
    }

    @Override protected BoundingArea generateFlattened() {
        RegionAxisAlignedBoundingBox ret = new RegionAxisAlignedBoundingBox();
        JsonObject init = new JsonObject();
        init.addProperty("world", world.getUID().toString());
        init.add("point1", new Point3I(minX, 0, minZ).asJsonObject());
        init.add("point2", new Point3I(maxX, 0, maxZ).asJsonObject());
        init.addProperty("region", region.getUid().toString());
        ret.initialize(init);
        return ret;
    }

}
