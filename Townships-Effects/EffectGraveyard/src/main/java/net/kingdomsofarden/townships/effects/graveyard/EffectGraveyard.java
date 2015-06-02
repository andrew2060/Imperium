package net.kingdomsofarden.townships.effects.graveyard;

import net.kingdomsofarden.townships.TownshipsPlugin;
import net.kingdomsofarden.townships.api.ITownshipsPlugin;
import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.effects.Effect;
import net.kingdomsofarden.townships.api.permissions.AccessType;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.util.StoredDataSection;
import net.kingdomsofarden.townships.util.LocationSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.ArrayList;
import java.util.Random;

public class EffectGraveyard implements Effect, Listener {

    private static Random rand = new Random();
    private static ArrayList<EffectGraveyard> activeEffects = new ArrayList<EffectGraveyard>();
    private Region region;
    private boolean global;
    private Location location;

    @Override public String getName() {
        return "graveyard";
    }

    @Override public void onInit(ITownshipsPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, (TownshipsPlugin) plugin);
    }

    @Override public void onLoad(ITownshipsPlugin plugin, Region region, StoredDataSection data) {
        this.region = region;
        this.global = Boolean.parseBoolean(data.get("global", "false"));
        this.location = data.get("respawn-location", new LocationSerializer(), null);
        if (this.global) {
            activeEffects.add(this);
        }
    }

    @Override public void onUnload(ITownshipsPlugin plugin, Region region, StoredDataSection data) {
        this.region = null;
        data.set("respawn-location", location, new LocationSerializer());
        if (this.global) {
            activeEffects.remove(this);
        }
    }

    @Override public Region getRegion() {
        return region;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Citizen c = Townships.getCitizens().getCitizen(event.getPlayer().getUniqueId());
        String preferred = c.getStoredEffectSettings(this).get("preferred", null);
        Region r = null;
        EffectGraveyard effect;
        if (preferred != null) {
            r = Townships.getRegions().get(preferred).orNull();
            if (r != null) {
                if (!r.hasAccess(c, AccessType.valueOf("RESPAWN"))) {
                    r = null;
                }
            }
            if (r != null && !r.hasEffect(getName())) {
                r = null;
            }
        }
        if (r == null) {
            effect = activeEffects.get(rand.nextInt(activeEffects.size()));
        } else {
            effect = r.getEffect(getName());
        }
        if (effect.location != null) {
            // TODO send message
            event.setRespawnLocation(effect.location);
        }

    }
}
