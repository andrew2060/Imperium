package net.kingdomsofarden.townships.api.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class BankEconomyTransactionEvent extends EconomyTransactionEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private String name;
    private double amount;
    private boolean cancelled;
    private TransactionType type;

    public BankEconomyTransactionEvent(String name, double amount, TransactionType type) {
        this.name = name;
        this.amount = amount;
        this.type = type;
        this.cancelled = false;
    }

    public static HandlerList getHandlerList() {
        return handlers;
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

    @Override public boolean isCancelled() {
        return cancelled;
    }

    @Override public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    public String getBankName() {
        return name;
    }
}
