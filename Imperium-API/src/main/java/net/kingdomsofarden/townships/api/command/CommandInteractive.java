package net.kingdomsofarden.townships.api.command;


import org.bukkit.command.CommandSender;

import java.util.Map;
import java.util.Stack;

public abstract class CommandInteractive implements Command {

    /**
     * Starts interactive command execution
     *
     * @param steps A stack of interactive steps to execute. This may be modified during execution
     */
    public void start(CommandSender sender, Stack<InteractiveCommandStep> steps, Map<String,
        Object> data) {
        if (!steps.empty()) {
            steps.pop().execute();
        } else {
            finish(sender, data);
        }
    }

    /**
     * Finishes Command Execution After All Interactive Steps Finish Execution
     */

    public abstract void finish(CommandSender sender, Map<String, Object> commandData);
}
