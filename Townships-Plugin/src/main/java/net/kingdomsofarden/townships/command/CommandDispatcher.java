package net.kingdomsofarden.townships.command;

import net.kingdomsofarden.townships.TownshipsPlugin;
import net.kingdomsofarden.townships.api.command.Command;
import net.kingdomsofarden.townships.util.I18N;
import net.kingdomsofarden.townships.util.Messaging;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;

public class CommandDispatcher implements CommandExecutor {

    private final TownshipsPlugin plugin;
    private HashMap<String, Command> commandsByIdentifier;

    public CommandDispatcher(TownshipsPlugin plugin) {
        this.plugin = plugin;

    }


    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command label, String s, String[] cmd) {
        for (int identifierLength = cmd.length; identifierLength >= 0; identifierLength--) {
            final StringBuilder identBuilder = new StringBuilder();
            identBuilder.append(s);
            for (int identIterator = 0; identIterator < identifierLength; identIterator++) {
                identBuilder.append(' ');
                identBuilder.append(cmd[identIterator]);
            }
            final String ident = identBuilder.toString().toLowerCase();
            // Find command
            final Command command = commandsByIdentifier.get(ident);
            if (command == null) {
                continue;
            }
            // Check permissions
            if (sender instanceof Player) {
                if (command.getPermission() != null && !sender.hasPermission(command.getPermission())) {
                    Messaging.sendFormattedMessage(sender, I18N.NO_PERMISSION_COMMAND, command.getPermission());
                    return true;
                }
            }
            // Remaining Arguments
            String[] args = Arrays.copyOfRange(cmd, identifierLength, cmd.length);
            // Check argument length
            int len = args.length;
            if (len < command.getMinArguments() || (command.getMaxArguments() != -1
                    && len > command.getMaxArguments())) {
                if (sender instanceof Player) {
                    Messaging.sendFormattedMessage(sender, I18N.COMMAND_IMPROPER_ARGUMENTS, command.getUsage());
                    return true;
                }
            }
            return command.execute(sender, args);
        }
        Messaging.sendFormattedMessage(sender, I18N.COMMAND_NOT_FOUND);
        return true;
    }
}
