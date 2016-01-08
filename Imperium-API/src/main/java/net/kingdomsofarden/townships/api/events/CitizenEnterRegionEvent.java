package net.kingdomsofarden.townships.api.events;

import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.regions.FunctionalRegion;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CitizenEnterRegionEvent extends Event {
    private final Citizen citizen;
    private final FunctionalRegion region;

    private static final HandlerList handlerList = new HandlerList();

    public CitizenEnterRegionEvent(Citizen citizen, FunctionalRegion region) {
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

    public FunctionalRegion getRegion() {
        return region;
    }
}
