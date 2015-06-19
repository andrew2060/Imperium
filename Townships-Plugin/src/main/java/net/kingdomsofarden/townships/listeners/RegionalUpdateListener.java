package net.kingdomsofarden.townships.listeners;

import com.google.common.collect.Sets;
import net.kingdomsofarden.townships.api.ITownshipsPlugin;
import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.events.CitizenEnterRegionEvent;
import net.kingdomsofarden.townships.api.events.CitizenExitRegionEvent;
import net.kingdomsofarden.townships.api.events.CitizenPreEnterRegionEvent;
import net.kingdomsofarden.townships.api.events.CitizenPreExitRegionEvent;
import net.kingdomsofarden.townships.api.regions.Area;
import net.kingdomsofarden.townships.api.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.plugin.PluginManager;

import java.util.HashSet;
import java.util.Set;

public class RegionalUpdateListener implements Listener {

    private final ITownshipsPlugin plugin;

    public RegionalUpdateListener(ITownshipsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler public void onPlayerJoin(PlayerJoinEvent event) {
        Citizen c = plugin.getCitizens().getCitizen(event.getPlayer().getUniqueId());
        WrapperEvent e = new WrapperEvent(); // Don't allow player joins to be cancelled
        if (runMovementEvents(e, c, event.getPlayer().getLocation())) {
            c.setCurrentArea(
                plugin.getRegions().getBoundingArea(event.getPlayer().getLocation()).orNull());
        } else {
            forceSpawn(c);
        }
    }

    @EventHandler public void onPlayerQuit(PlayerQuitEvent event) {
        Citizen c = plugin.getCitizens().getCitizen(event.getPlayer().getUniqueId());
        c.setCurrentArea(null);
        c.getActiveRegions().forEach(r -> { // Not cancellable
            Bukkit.getPluginManager().callEvent(new CitizenPreExitRegionEvent(c, r));
            Bukkit.getPluginManager().callEvent(new CitizenExitRegionEvent(c, r));
        });
        c.setActiveRegions(new HashSet<>());
    }

    @EventHandler public void onPlayerDeath(PlayerDeathEvent event) {
        Citizen c = plugin.getCitizens().getCitizen(event.getEntity().getUniqueId());
        c.setCurrentArea(null);
        c.getActiveRegions().stream().forEach(r -> { // Not cancellable
            Bukkit.getPluginManager().callEvent(new CitizenPreExitRegionEvent(c, r));
            Bukkit.getPluginManager().callEvent(new CitizenExitRegionEvent(c, r));
        });
        c.setActiveRegions(new HashSet<>());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Citizen c = plugin.getCitizens().getCitizen(event.getPlayer().getUniqueId());
        WrapperEvent e = new WrapperEvent();
        if (!runMovementEvents(e, c, event.getRespawnLocation())) {
            forceSpawn(c);
        } else {
            c.setCurrentArea(
                plugin.getRegions().getBoundingArea(event.getRespawnLocation()).orNull());
        }
    }

    @EventHandler(ignoreCancelled = true) public void onPlayerTeleport(PlayerTeleportEvent event) {
        Location to = event.getTo();
        Citizen c = plugin.getCitizens().getCitizen(event.getPlayer().getUniqueId());
        if (to.getWorld().getSpawnLocation().equals(to)) { // Spawned to default
            c.setCurrentArea(plugin.getRegions().getBoundingArea(to).orNull());
            Set<Region> oldActive = c.getActiveRegions();
            Set<Region> currActive = c.getCurrentArea()
                .getBoundingRegions(to.getBlockX(), to.getBlockY(), to.getBlockZ());
            Set<Region> entered = Sets.difference(currActive, oldActive);
            Set<Region> exited = Sets.difference(oldActive, currActive);
            PluginManager pm = Bukkit.getPluginManager();
            entered.stream().forEach(r -> {
                pm.callEvent(new CitizenPreEnterRegionEvent(c, r));
                pm.callEvent(new CitizenEnterRegionEvent(c, r));
            });
            exited.stream().forEach(r -> {
                pm.callEvent(new CitizenPreExitRegionEvent(c, r));
                pm.callEvent(new CitizenExitRegionEvent(c, r));
            });
            c.setActiveRegions(currActive);
        } else {
            if (runMovementEvents(event, c, to)) {
                c.setCurrentArea(plugin.getRegions().getBoundingArea(to).orNull());
            } else { // Don't follow vehicle through a teleportation
                Entity v = event.getPlayer().getVehicle();
                if (v != null) {
                    v.eject();
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true) public void onVehicleEnter(VehicleEnterEvent event) {
        Entity e = event.getEntered();
        if (e instanceof Player) {
            Citizen c = plugin.getCitizens().getCitizen(e.getUniqueId());
            Location to = event.getVehicle().getLocation();
            if (runMovementEvents(event, c, to)) {
                c.setCurrentArea(plugin.getRegions().getBoundingArea(to).orNull());
            }
        }
    }

    private void forceSpawn(Citizen c) {
        Player p = Bukkit.getPlayer(c.getUid());
        p.teleport(p.getLocation().getWorld().getSpawnLocation(),
            PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    @EventHandler(ignoreCancelled = true) public void onPlayerMove(PlayerMoveEvent event) {
        Location to = getBlockLoc(event.getTo());
        Location from = getBlockLoc(event.getFrom());
        if (to.equals(from)) { // Same block
            return;
        }
        Citizen c = plugin.getCitizens().getCitizen(event.getPlayer().getUniqueId());
        if (runMovementEvents(event, c, to)) {
            Area a = c.getCurrentArea();
            if (a == null || !a.isInBounds(to)) {
                c.setCurrentArea(plugin.getRegions().getBoundingArea(to).orNull());
            }
        } else {
            Entity v = event.getPlayer().getVehicle();
            if (v != null) {
                v.eject();
            }
        }
    }

    private boolean runMovementEvents(Cancellable event, Citizen c, Location to) {
        // TODO use current area instead of whole tree
        Set<Region> oldActive = c.getActiveRegions();
        Set<Region> currActive = Townships.getRegions().getBoundingRegions(to);
        Set<Region> entered = Sets.difference(currActive, oldActive);
        Set<Region> exited = Sets.difference(oldActive, currActive);
        PluginManager pm = Bukkit.getPluginManager();
        for (Region r : entered) {
            CitizenPreEnterRegionEvent e = new CitizenPreEnterRegionEvent(c, r);
            pm.callEvent(e);
            if (e.isCancelled()) {
                event.setCancelled(true);
            }
        }
        for (Region r : exited) {
            CitizenPreExitRegionEvent e = new CitizenPreExitRegionEvent(c, r);
            pm.callEvent(e);
            if (e.isCancelled()) {
                event.setCancelled(true);
            }
        }
        if (!event.isCancelled()) {
            entered.stream().forEach(r -> pm.callEvent(new CitizenEnterRegionEvent(c, r)));
            exited.stream().forEach(r -> pm.callEvent(new CitizenExitRegionEvent(c, r)));
            c.setActiveRegions(currActive);
            return true;
        } else {
            return false;
        }
    }

    private Location getBlockLoc(Location loc) {
        return new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    private static class WrapperEvent implements Cancellable {

        private boolean cancel = false;

        @Override public boolean isCancelled() {
            return cancel;
        }

        @Override public void setCancelled(boolean b) {
            this.cancel = b;
        }
    }
}
