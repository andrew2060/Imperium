package net.kingdomsofarden.townships.api.events;

import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.regions.FunctionalRegion;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RegionCreateEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private FunctionalRegion region;
    private Citizen citizen;

    public RegionCreateEvent(Citizen citizen, FunctionalRegion region) {
        this.citizen = citizen;
        this.region = region;
        this.cancelled = false;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * @return The citizen creating the region
     */
    public Citizen getCitizen() {
        return citizen;
    }

    /**
     * @return The region being created
     */
    public FunctionalRegion getRegion() {
        return region;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
