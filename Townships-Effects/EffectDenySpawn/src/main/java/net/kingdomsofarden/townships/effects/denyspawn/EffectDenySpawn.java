package net.kingdomsofarden.townships.effects.denyspawn;

import net.kingdomsofarden.townships.TownshipsPlugin;
import net.kingdomsofarden.townships.api.ITownshipsPlugin;
import net.kingdomsofarden.townships.api.effects.Effect;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.util.Serializer;
import net.kingdomsofarden.townships.api.util.StoredDataSection;
import net.kingdomsofarden.townships.effects.denyspawn.listener.SpawnListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;

import java.util.HashSet;

public class EffectDenySpawn implements Effect {

    private HashSet<EntityType> deny;
    private Region region;

    @Override
    public String getName() {
        return "deny-spawn";
    }

    @Override
    public void onInit(ITownshipsPlugin plugin) {
        TownshipsPlugin impl = plugin.getBackingImplementation();
        Bukkit.getPluginManager().registerEvents(new SpawnListener(), impl);
    }

    @Override
    public void onLoad(ITownshipsPlugin plugin, Region region, StoredDataSection data) {
        deny = new HashSet<EntityType>();
        this.region = region;
        for (String key : data.getList("types")) {
            EntityType type = EntityType.valueOf(key.toUpperCase());
            deny.add(type);
        }
    }

    @Override
    public void onUnload(ITownshipsPlugin plugin, Region region, StoredDataSection data) {
        data.set("types", deny, new Serializer<EntityType>() {
            @Override
            public String serialize(EntityType obj) {
                return obj.name();
            }

            @Override
            public EntityType deserialize(String input) {
                return EntityType.valueOf(input.toUpperCase());
            }
        });
        deny = null;
        this.region = null;
    }

    @Override
    public Region getRegion() {
        return region;
    }

    public boolean isDenied(EntityType type) {
        return deny.contains(type);
    }
}
