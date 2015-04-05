package net.kingdomsofarden.townships.listeners;

import net.kingdomsofarden.townships.TownshipsPlugin;
import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.permissions.AccessType;
import net.kingdomsofarden.townships.api.permissions.RoleGroup;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.util.Constants;
import net.kingdomsofarden.townships.util.I18N;
import net.kingdomsofarden.townships.util.Messaging;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Collection;
import java.util.HashSet;

public class BlockProtectionListener implements Listener {

    private TownshipsPlugin plugin;

    public BlockProtectionListener(TownshipsPlugin plugin) {
        this.plugin = plugin;
    }


    private boolean hasAccess(Collection<Region> boundingRegions, Citizen c, AccessType access) {
        HashSet<RoleGroup> effectiveRoles = new HashSet<RoleGroup>();
        for (Region region : boundingRegions) {
            effectiveRoles.addAll(region.getRoles(c));
            if (region.hasEffect("protection")) {
                if (region.hasAccess(c, access, effectiveRoles)) {
                    return true;
                }
            }
        }
        return false;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            Block b = event.getClickedBlock();
            if (Constants.ACCESS_TYPES.contains(b.getType())) {
                Collection<Region> boundingRegions = plugin.getRegions().getBoundingRegions(event.getClickedBlock().getLocation());
                Citizen c = Townships.getCitizens().getCitizen(event.getPlayer().getUniqueId());
                if (!hasAccess(boundingRegions, c, AccessType.ACCESS)) {
                    event.setCancelled(true);
                    Messaging.sendFormattedMessage(event.getPlayer(), I18N.NO_PERMISSION_AREA_ACCESS);
                }
            } else if (Constants.INTERACT_TYPES.contains(b.getType())
                    || (Constants.PROTECT_FIRE && event.getItem() != null && event.getItem().getType().equals(Material.FLINT_AND_STEEL))) {
                Collection<Region> boundingRegions = plugin.getRegions().getBoundingRegions(event.getClickedBlock().getLocation());
                Citizen c = Townships.getCitizens().getCitizen(event.getPlayer().getUniqueId());
                if (!hasAccess(boundingRegions, c, AccessType.INTERACT)) {
                    event.setCancelled(true);
                    Messaging.sendFormattedMessage(event.getPlayer(), I18N.NO_PERMISSION_AREA_INTERACT);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Collection<Region> boundingRegions = plugin.getRegions().getBoundingRegions(event.getBlock().getLocation());
        Citizen c = Townships.getCitizens().getCitizen(event.getPlayer().getUniqueId());
        if (!hasAccess(boundingRegions, c, AccessType.CONSTRUCT)) {
            event.setCancelled(true);
            Messaging.sendFormattedMessage(event.getPlayer(), I18N.NO_PERMISSION_AREA_CONSTRUCT);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Collection<Region> boundingRegions = plugin.getRegions().getBoundingRegions(event.getBlock().getLocation());
        Citizen c = Townships.getCitizens().getCitizen(event.getPlayer().getUniqueId());
        if (!hasAccess(boundingRegions, c, AccessType.CONSTRUCT)) {
            event.setCancelled(true);
            Messaging.sendFormattedMessage(event.getPlayer(), I18N.NO_PERMISSION_AREA_CONSTRUCT);
        }
    }
}
