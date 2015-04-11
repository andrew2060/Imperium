package net.kingdomsofarden.townships.effects.core;

import net.kingdomsofarden.townships.TownshipsPlugin;
import net.kingdomsofarden.townships.api.ITownshipsPlugin;
import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.effects.Effect;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.util.StoredDataSection;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class EffectGraveyard implements Effect, Listener {

    private Region region;
    private boolean global;

    @Override
    public String getName() {
        return "graveyard";
    }

    @Override
    public void onInit(ITownshipsPlugin plugin) {

    }

    @Override
    public void onLoad(ITownshipsPlugin plugin, Region region, StoredDataSection data) {
        this.region = region;
        this.global = Boolean.parseBoolean(data.get("global", "false"));
        Bukkit.getPluginManager().registerEvents(this, (TownshipsPlugin) plugin);
    }

    @Override
    public void onUnload(ITownshipsPlugin plugin, Region region, StoredDataSection data) {
        this.region = null;
    }

    @Override
    public Region getRegion() {
        return region;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Citizen c = Townships.getCitizens().getCitizen(event.getPlayer().getUniqueId());
        if (global) {

        } else {

        }
    }
}
