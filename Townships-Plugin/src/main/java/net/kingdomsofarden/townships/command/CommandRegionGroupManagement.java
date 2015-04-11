package net.kingdomsofarden.townships.command;

import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.command.Command;
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

public class CommandRegionGroupManagement implements Command {
    @Override
    public String[] getIdentifiers() {
        return new String[] {"region manage group", "town manage group"};
    }

    @Override
    public String getPermission() {
        return "townships.region.manage";
    }

    @Override
    public int getMaxArguments() {
        return 4;
    }

    @Override
    public int getMinArguments() {
        return 4;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        String type = args[0].toLowerCase();
        if (type.equals("add") || type.equals("remove")) {
            boolean addOp = type.equals("add");
            Region r = Townships.getRegions().get(args[1]).orNull();
            if (r == null) {
                Messaging.sendFormattedMessage(sender, I18N.REGION_NOT_FOUND, args[1].toLowerCase());
                return true;
            }
            if (sender instanceof Player) {
                Citizen c = Townships.getCitizens().getCitizen(((Player) sender).getUniqueId());
                Collection<Region> intersections = Townships.getRegions().getIntersectingRegions(r.getBounds());
                boolean perm = false;
                HashSet<RoleGroup> effective = new HashSet<RoleGroup>();
                for (Region found : intersections) {
                    if (r.getTier() <= found.getTier()) {
                        effective.addAll(r.getRoles(c));
                        if (r.hasAccess(c, AccessType.GOVERNOR, effective)) {
                            perm = true;
                            break;
                        }
                    }
                }
                if (!perm) {
                    Messaging.sendFormattedMessage(sender, I18N.NO_PERMISSION_AREA_GOVERN);
                    return true;
                }
            }

            RoleGroup group = RoleGroup.valueOf(args[2]);
            Player p = Bukkit.getPlayer(args[3]);
            if (p == null) {
                Messaging.sendFormattedMessage(sender, I18N.PLAYER_NOT_FOUND, args[3]);
                return true;
            }
            if (addOp) {
                r.addRole(Townships.getCitizens().getCitizen(p.getUniqueId()), group);
                Messaging.sendFormattedMessage(sender, I18N.COMMAND_COMPLETED_SUCCESSFULLY);
                return true;
            } else {
                if (r.removeRole(Townships.getCitizens().getCitizen(p.getUniqueId()), group)) {
                    Messaging.sendFormattedMessage(sender, I18N.COMMAND_COMPLETED_SUCCESSFULLY);
                    return true;
                } else {
                    Messaging.sendFormattedMessage(sender, I18N.ROLE_NOT_PRESENT, p.getName(), group.toString(), r.getName().or(r.getUid().toString()));
                    return true;
                }
            }
        } else {
            Messaging.sendFormattedMessage(sender, I18N.INVALID_USAGE, getUsage());
            return true;
        }
    }

    @Override
    public String getUsage() {
        return "/region manage group <add|remove> <regionname> <rolename> <playername>";
    }
}
