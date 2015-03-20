package net.kingdomsofarden.townships.listeners;

import net.kingdomsofarden.townships.api.ITownshipsPlugin;
import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.regions.Area;
import net.kingdomsofarden.townships.util.Constants;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerListener implements Listener {

    private static final int[][] DIRECTIONS = new int[][] {new int[] {1,2,3}, new int[] {0,-1,4}, new int[] {7,6,5}};

    private final ITownshipsPlugin plugin;

    public PlayerListener(ITownshipsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Citizen c = plugin.getCitizens().getCitizen(event.getPlayer().getUniqueId());
        Area a = c.getCurrentArea();
        if (event instanceof PlayerTeleportEvent) { // Always Recalculate This
            c.setCurrentArea(plugin.getRegions().getBoundingArea(event.getTo()).orNull());
        } else if (a == null || !a.isInBounds(event.getTo())) {
            Location to = event.getTo();
            Location from = event.getFrom();
            double xFrom;
            double xTo;
            double zFrom;
            double zTo;
            xFrom = from.getX() % Constants.MIN_DIV_X;
            xTo = to.getX() % Constants.MIN_DIV_X;
            zFrom = from.getZ() % Constants.MIN_DIV_Z;
            zTo = to.getZ() % Constants.MIN_DIV_Z;
            if (xFrom != xTo || zFrom != zTo) {
                if (a == null) {
                    c.setCurrentArea(plugin.getRegions().getBoundingArea(to).orNull());
                } else {
                    c.setCurrentArea(plugin.getRegions().getBoundingArea(to).orNull());
                }
            }
        }
    }
}
