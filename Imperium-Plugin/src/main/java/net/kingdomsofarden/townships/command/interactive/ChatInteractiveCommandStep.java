package net.kingdomsofarden.townships.command.interactive;

import net.kingdomsofarden.townships.api.command.CommandInteractive;
import net.kingdomsofarden.townships.api.command.InteractiveCommandStep;
import net.kingdomsofarden.townships.listeners.ChatBasedInteractiveCommandListener;
import net.kingdomsofarden.townships.util.I18N;
import net.kingdomsofarden.townships.util.Messaging;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Stack;

public abstract class ChatInteractiveCommandStep extends InteractiveCommandStep {

    public ChatInteractiveCommandStep(CommandInteractive command, CommandSender sender, Stack<InteractiveCommandStep> stack, Map<String, Object> commandData) {
        super(command, sender, stack, commandData);
    }

    @Override
    public void execute() {
        if (!(sender instanceof Player)) {
            Messaging.sendFormattedMessage(sender, I18N.PLAYER_ONLY_COMMAND);
            return;
        }
        Messaging.sendFormattedMessage(sender, getPrompt(sender));
        ChatBasedInteractiveCommandListener.register(((Player)sender).getUniqueId(), this);
    }

    public void process(String input) {
        if (input.equalsIgnoreCase("!cancel")) {
            return;
        } else {
            if (validate(input)) {
                data.put(getIdentifier(), input);
            } else {
                Messaging.sendFormattedMessage(sender, getValidInputPrompt(sender));
                stack.push(this);
            }
        }
        rejoin();
    }

    public abstract boolean validate(String input);

    public abstract String getValidInputPrompt(CommandSender sender);

    public abstract String getIdentifier();

    public abstract String getPrompt(CommandSender sender);
}
