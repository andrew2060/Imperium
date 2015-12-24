package net.kingdomsofarden.townships.effects.denyexplosions;

import net.kingdomsofarden.townships.api.ITownshipsPlugin;
import net.kingdomsofarden.townships.api.effects.Effect;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.util.StoredDataSection;
import net.kingdomsofarden.townships.effects.denyexplosions.listener.ExplosiveProtectionListener;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class EffectDenyExplosions implements Effect, Listener {

    private Region region;

    @Override public String getName() {
        return "deny-explosions";
    }

    @Override public void onInit(ITownshipsPlugin plugin) {
        Bukkit.getPluginManager()
            .registerEvents(new ExplosiveProtectionListener(), (Plugin) plugin);
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
