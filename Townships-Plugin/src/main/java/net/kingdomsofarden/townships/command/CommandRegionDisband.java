package net.kingdomsofarden.townships.command;

import com.google.common.base.Optional;
import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.command.Command;
import net.kingdomsofarden.townships.api.events.RegionDisbandEvent;
import net.kingdomsofarden.townships.api.events.RegionDisbandEvent.DisbandCause;
import net.kingdomsofarden.townships.api.permissions.AccessType;
import net.kingdomsofarden.townships.api.permissions.RoleGroup;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.util.I18N;
import net.kingdomsofarden.townships.util.Messaging;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public class CommandRegionDisband implements Command {
    @Override
    public String[] getIdentifiers() {
        return new String[] {"region disband, town disband, nation disband"};
    }

    @Override
    public String getPermission() {
        return "townships.region.create";
    }

    @Override
    public int getMaxArguments() {
        return 1;
    }

    @Override
    public int getMinArguments() {
        return 1;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Optional<Region> toRemove;
        try {
            toRemove = Townships.getRegions().get(UUID.fromString(args[0]));
        } catch (IllegalArgumentException e) {
            toRemove = Townships.getRegions().get(args[0]);
        }
        if (toRemove.isPresent()) {
            Region r = toRemove.get();
            // Check permission
            if (sender instanceof Player) {
                Citizen c = Townships.getCitizens().getCitizen(((Player) sender).getUniqueId());
                Collection<Region> intersections = Townships.getRegions().getIntersectingRegions(r.getBounds());
                boolean perm = false;
                HashSet<RoleGroup> effective = new HashSet<RoleGroup>();
                for (Region found : intersections) {
                    if (r.getTier() <= found.getTier()) {
                        effective.addAll(r.getRoles(c));
                        if (r.hasAccess(c, AccessType.ZONING, effective)) {
                            perm = true;
                            break;
                        }
                    }
                }
                if (!perm) {
                    Messaging.sendFormattedMessage(sender, I18N.NO_PERMISSION_AREA_ZONING);
                    return true;
                }
            }
            RegionDisbandEvent event = new RegionDisbandEvent(r, DisbandCause.COMMAND);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                if (Townships.getRegions().remove(toRemove.get())) {
                    Messaging.sendFormattedMessage(sender, I18N.COMMAND_COMPLETED_SUCCESSFULLY);
                    return true;
                } else {
                    Messaging.sendFormattedMessage(sender, I18N.INTERNAL_ERROR);
                    return true;
                }
            } else {
                return true;
            }
        } else {
            Messaging.sendFormattedMessage(sender, I18N.REGION_NOT_FOUND, args[0]);
            return true;
        }
    }

    @Override
    public String getUsage() {
        return "/region disband <regionname|regionuid>";
    }
}
