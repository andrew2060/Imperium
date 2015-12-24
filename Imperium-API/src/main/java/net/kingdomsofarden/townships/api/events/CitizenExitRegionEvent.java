package net.kingdomsofarden.townships.api.events;

import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.regions.Region;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CitizenExitRegionEvent extends Event {
    private final Citizen citizen;
    private final Region region;

    private static final HandlerList handlerList = new HandlerList();

    public CitizenExitRegionEvent(Citizen citizen, Region region) {
        this.citizen = citizen;
        this.region = region;
    }

    @Override public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public Citizen getCitizen() {
        return citizen;
    }

    public Region getRegion() {
        return region;
    }
}
