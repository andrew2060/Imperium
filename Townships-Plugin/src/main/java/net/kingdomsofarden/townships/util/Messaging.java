package net.kingdomsofarden.townships.util;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class Messaging {
    public static void sendFormattedMessage(CommandSender recipient, String message, Object... args) {
        message = I18N.GLOBAL_PREFIX + message;
        for (int i = 0; i < args.length; i++) {
            message.replace("$" + i, args[i] + "");
        }
        recipient.sendMessage(message);
    }

    public static void broadcastFormattedMessage(String message, Object... args) {
        message = I18N.GLOBAL_PREFIX + message;
        for (int i = 0; i < args.length; i++) {
            message.replace("$" + i, args[i] + "");
        }
        Bukkit.broadcastMessage(message);
    }
}
