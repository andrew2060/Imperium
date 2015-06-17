package net.kingdomsofarden.townships.api.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Map;

public class ProductionEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private Map<String, Integer> itemProduction;
    private double econProduction;
    private boolean cancelled;

    public ProductionEvent(Map<String, Integer> itemProduction, double econProduction) {
        this.itemProduction = itemProduction;
        this.econProduction = econProduction;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override public boolean isCancelled() {
        return cancelled;
    }

    @Override public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override public HandlerList getHandlers() {
        return handlers;
    }

    public Map<String, Integer> getItemProduction() {
        return itemProduction;
    }

    public double getEconProduction() {
        return econProduction;
    }

    public void setEconProduction(double econProduction) {
        this.econProduction = econProduction;
    }
}
