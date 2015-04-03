package net.kingdomsofarden.townships.util;

import net.kingdomsofarden.townships.api.util.Serializer;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class LocationSerializer implements Serializer<Location> {
    @Override
    public String serialize(Location obj) {
        StringBuilder sB = new StringBuilder();
        boolean first = true;
        for (Entry<String, Object> e : obj.serialize().entrySet()) {
            if (first) {
                first = false;
            } else {
                sB.append("::");
            }
            sB.append(e.getKey());
            sB.append(":");
            sB.append(e.getValue());
        }
        return sB.toString();
    }

    @Override
    public Location deserialize(String input) {
        Map<String, Object> deserialized = new HashMap<String, Object>();
        for (String entry : input.split("::")) {
            String[] kv = entry.split(":");
            deserialized.put(kv[0], kv[1]);
        }
        return Location.deserialize(deserialized);
    }
}
