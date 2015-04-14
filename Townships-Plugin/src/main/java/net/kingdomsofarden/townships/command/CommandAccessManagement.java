package net.kingdomsofarden.townships.command;

import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.api.command.Command;
import net.kingdomsofarden.townships.api.permissions.AccessType;
import net.kingdomsofarden.townships.api.permissions.RoleGroup;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.util.I18N;
import net.kingdomsofarden.townships.util.Messaging;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class CommandAccessManagement implements Command {
    @Override
    public String[] getIdentifiers() {
        return new String[] {"region manage access", "town manage access"};
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
        Region r = Townships.getRegions().get(args[1]).orNull();
        if (r == null) {
            Messaging.sendFormattedMessage(sender, I18N.REGION_NOT_FOUND, args[1].toLowerCase());
            return true;
        }
        if (args[1].equalsIgnoreCase("group")) {
            RoleGroup group = RoleGroup.valueOf(args[2]);
            AccessType access = AccessType.valueOf(args[4]);
            if (args[3].equalsIgnoreCase("add")) {
                if (r.addAccess(group, access)) {
                    Messaging.sendFormattedMessage(sender, I18N.COMMAND_COMPLETED_SUCCESSFULLY);
                    return true;
                } else {
                    Messaging.sendFormattedMessage(sender, I18N.ACCESS_NOT_PRESENT);
                    return true;
                }
            } else if (args[3].equalsIgnoreCase("remove")) {
                if (r.removeAccess(group, access)) {
                    Messaging.sendFormattedMessage(sender, I18N.COMMAND_COMPLETED_SUCCESSFULLY);
                    return true;
                } else {
                    Messaging.sendFormattedMessage(sender, I18N.ACCESS_NOT_PRESENT);
                    return true;
                }
            }
            return true;
        } else if (args[1].equalsIgnoreCase("user")) {
            OfflinePlayer p = Bukkit.getOfflinePlayer(args[2]);
            if (p == null) {
                Messaging.sendFormattedMessage(sender, I18N.PLAYER_NOT_FOUND);
            }
            UUID playerUid = p.getUniqueId();
            AccessType access = AccessType.valueOf(args[4]);
            if (args[3].equalsIgnoreCase("add")) {
                if (r.addAccess(playerUid, access)) {
                    Messaging.sendFormattedMessage(sender, I18N.COMMAND_COMPLETED_SUCCESSFULLY);
                    return true;
                } else {
                    Messaging.sendFormattedMessage(sender, I18N.ACCESS_NOT_PRESENT);
                    return true;
                }
            } else if (args[3].equalsIgnoreCase("remove")) {
                if (r.removeAccess(playerUid, access)) {
                    Messaging.sendFormattedMessage(sender, I18N.COMMAND_COMPLETED_SUCCESSFULLY);
                    return true;
                } else {
                    Messaging.sendFormattedMessage(sender, I18N.ACCESS_NOT_PRESENT);
                    return true;
                }
            }
            return true;
        } else {
            return true;
        }
    }

    @Override
    public String getUsage() {
        return "/region manage access <region> <group|user> <name> <add|remove> <access>";
    }
}
