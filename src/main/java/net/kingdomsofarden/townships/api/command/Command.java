package net.kingdomsofarden.townships.api.command;

import org.bukkit.command.CommandSender;

public interface Command {

    String[] getIdentifiers();

    String getPermission();

    int getMaxArguments();

    int getMinArguments();

    boolean execute(CommandSender sender, String[] args);

    String getUsage();
}
