package net.kingdomsofarden.townships.api.events;

import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.regions.Region;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RegionCreateEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private Region region;
    private Citizen citizen;

    public RegionCreateEvent(Citizen citizen, Region region) {
        this.citizen = citizen;
        this.region = region;
        this.cancelled = false;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
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
    public Region getRegion() {
        return region;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
