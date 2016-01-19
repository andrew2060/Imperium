package net.kingdomsofarden.townships.util;

import com.google.gson.JsonObject;
import net.kingdomsofarden.townships.api.regions.bounds.BoundingArea;

public class BoundsJsonDeserializer {
    public static BoundingArea deserialize(JsonObject object) {
        switch (object.get("type").getAsString()) {
            case "AABB": {
                break;
            }
            case "POLY": {
                break;
            }
            case "COMPOSITE": {
                break;
            }
        }
    }
}
