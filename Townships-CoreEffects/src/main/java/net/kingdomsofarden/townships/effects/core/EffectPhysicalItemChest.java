package net.kingdomsofarden.townships.effects.core;

import net.kingdomsofarden.townships.api.ITownshipsPlugin;
import net.kingdomsofarden.townships.api.effects.Effect;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.util.Serializer;
import net.kingdomsofarden.townships.api.util.StoredDataSection;
import net.kingdomsofarden.townships.resources.PhysicalChestItemProvider;
import net.kingdomsofarden.townships.util.LocationSerializer;

public class EffectPhysicalItemChest extends PhysicalChestItemProvider implements Effect {

    private Region region;

    @Override
    public String getName() {
        return "physical-resource-chest";
    }

    @Override
    public void onInit(ITownshipsPlugin plugin) {

    }

    @Override
    public void onLoad(ITownshipsPlugin plugin, Region r, StoredDataSection data) {
        region = r;
        chestLocation = data.get("location", new LocationSerializer(), null);
        priority = data.get("priority", new Serializer<Integer>() {
            @Override
            public String serialize(Integer obj) {
                return obj + "";
            }

            @Override
            public Integer deserialize(String input) {
                return Integer.valueOf(input);
            }
        }, -1);
    }

    @Override
    public void onUnload(ITownshipsPlugin plugin, Region region, StoredDataSection data) {
        data.set("location", chestLocation, new LocationSerializer());
        data.set("priority", priority);
    }

    @Override
    public Region getRegion() {
        return region;
    }
}
