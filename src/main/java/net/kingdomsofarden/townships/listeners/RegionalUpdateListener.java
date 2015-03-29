package net.kingdomsofarden.townships.listeners;

import net.kingdomsofarden.townships.api.ITownshipsPlugin;
import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.regions.Area;
import net.kingdomsofarden.townships.util.Constants;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class RegionalUpdateListener implements Listener {

    private final ITownshipsPlugin plugin;

    public RegionalUpdateListener(ITownshipsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Citizen c = plugin.getCitizens().getCitizen(event.getPlayer().getUniqueId());
        c.setCurrentArea(plugin.getRegions().getBoundingArea(event.getPlayer().getLocation()).orNull());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Citizen c = plugin.getCitizens().getCitizen(event.getPlayer().getUniqueId());
        c.setCurrentArea(null);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Citizen c = plugin.getCitizens().getCitizen(event.getEntity().getUniqueId());
        c.setCurrentArea(null);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled =  true)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Citizen c = plugin.getCitizens().getCitizen(event.getPlayer().getUniqueId());
        c.setCurrentArea(plugin.getRegions().getBoundingArea(event.getRespawnLocation()).orNull());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Citizen c = plugin.getCitizens().getCitizen(event.getPlayer().getUniqueId());
        Area a = c.getCurrentArea();
        if (a == null || !a.isInBounds(event.getTo())) {
            Location to = event.getTo();
            Location from = event.getFrom();
            double xFrom;
            double xTo;
            double zFrom;
            double zTo;
            xFrom = from.getX()/Constants.MIN_DIV_X;
            xTo = to.getX()/Constants.MIN_DIV_X;
            zFrom = from.getZ()/Constants.MIN_DIV_Z;
            zTo = to.getZ()/Constants.MIN_DIV_Z;
            if (xFrom != xTo || zFrom != zTo) {
                c.setCurrentArea(plugin.getRegions().getBoundingArea(to).orNull());
            }
        }
    }
}
