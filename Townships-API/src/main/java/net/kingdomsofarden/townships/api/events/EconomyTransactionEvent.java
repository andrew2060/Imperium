package net.kingdomsofarden.townships.api.events;

import org.bukkit.event.Event;

public abstract class EconomyTransactionEvent extends Event {
    public enum TransactionType {
        DEPOSIT,
        WITHDRAW,
        TRANSFER
    }
}
