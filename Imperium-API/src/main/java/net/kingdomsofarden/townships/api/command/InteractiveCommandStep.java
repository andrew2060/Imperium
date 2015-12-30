package net.kingdomsofarden.townships.api.command;

import org.bukkit.command.CommandSender;

import java.util.Map;
import java.util.Stack;

/**
 * Represents a step within an interactive command that can be used to query/retrieve
 */
public abstract class InteractiveCommandStep {

    protected final Stack<InteractiveCommandStep> stack;
    protected final CommandSender sender;
    protected final CommandInteractive command;
    protected final Map<String, Object> data;

    public InteractiveCommandStep(CommandInteractive command, CommandSender sender,
        Stack<InteractiveCommandStep> stack, Map<String, Object> data) {
        this.command = command;
        this.sender = sender;
        this.stack = stack;
        this.data = data;
    }

    /**
     * Begins execution of this command step (i.e. prompting and registering for a listener of
     * some sort)
     */
    public abstract void execute();

    /**
     * Called to finish this interactive command step and continue: implementations are
     * responsible for calling this
     */
    public void rejoin() {
        command.start(sender, stack, data);
    }
}
