package net.kingdomsofarden.townships.api.events;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class PlayerEconomyTransactionEvent extends EconomyTransactionEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private OfflinePlayer player;
    private double amount;
    private boolean cancelled;
    private TransactionType type;

    public PlayerEconomyTransactionEvent(OfflinePlayer player, double amount, TransactionType type) {
        this.player = player;
        this.amount = amount;
        this.type = type;
        this.cancelled = false;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }


    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}
