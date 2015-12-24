package net.kingdomsofarden.townships.command;

import net.kingdomsofarden.townships.TownshipsPlugin;
import net.kingdomsofarden.townships.api.command.Command;
import net.kingdomsofarden.townships.command.selection.SelectionManager;
import net.kingdomsofarden.townships.util.I18N;
import net.kingdomsofarden.townships.util.Messaging;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommandToggleSelection implements Command, Listener {

    private Map<UUID, Boolean> cache;

    public CommandToggleSelection(TownshipsPlugin plugin) {
        cache = new HashMap<UUID, Boolean>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override public String[] getIdentifiers() {
        return new String[] {"region select toggle", "town select toggle"};
    }

    @Override public String getPermission() {
        return "townships.region.create";
    }

    @Override public int getMaxArguments() {
        return 0;
    }

    @Override public int getMinArguments() {
        return 0;
    }

    @Override public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            Messaging.sendFormattedMessage(sender, I18N.PLAYER_ONLY_COMMAND);
            return true;
        }
        boolean val = !cache.getOrDefault(((Player) sender).getUniqueId(), false);
        cache.put(((Player) sender).getUniqueId(), val);
        Messaging
            .sendFormattedMessage(sender, val ? I18N.SELECTION_ENABLED : I18N.SELECTION_DISABLED);
        return true;
    }

    @Override public String getUsage() {
        return "region select toggle";
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        cache.put(event.getPlayer().getUniqueId(), false);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        cache.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerKick(PlayerKickEvent event) {
        cache.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        UUID pid = event.getPlayer().getUniqueId();
        if (cache.getOrDefault(pid, false)) {
            Action a = event.getAction();
            if (a == Action.RIGHT_CLICK_BLOCK || a == Action.LEFT_CLICK_BLOCK) {
                event.setCancelled(true);
                Location loc = event.getClickedBlock().getLocation();
                int type = 0;
                if (a == Action.RIGHT_CLICK_BLOCK) {
                    SelectionManager.selections.getUnchecked(pid).setLoc2(loc);
                    type = 2;
                } else {
                    SelectionManager.selections.getUnchecked(pid).setLoc1(loc);
                    type = 1;
                }
                Messaging.sendFormattedMessage(event.getPlayer(), I18N.SELECTION_SET, type,
                    loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            }

        }
    }
}
