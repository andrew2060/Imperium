package net.kingdomsofarden.townships.effects.protection;

import net.kingdomsofarden.townships.TownshipsPlugin;
import net.kingdomsofarden.townships.api.ITownshipsPlugin;
import net.kingdomsofarden.townships.api.effects.Effect;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.util.StoredDataSection;
import net.kingdomsofarden.townships.effects.protection.listener.BlockProtectionListener;
import org.bukkit.Bukkit;

public class EffectProtection implements Effect {

    private Region region;

    @Override public String getName() {
        return "protection";
    }

    @Override public void onInit(ITownshipsPlugin plugin) {
        TownshipsPlugin p = (TownshipsPlugin) plugin;
        Bukkit.getPluginManager().registerEvents(new BlockProtectionListener(p), p);
    }

    @Override public void onLoad(ITownshipsPlugin plugin, Region region, StoredDataSection data) {
        this.region = region;
    }

    @Override public void onUnload(ITownshipsPlugin plugin, Region region, StoredDataSection data) {
        this.region = null;
    }

    @Override public Region getRegion() {
        return region;
    }

}
