package net.kingdomsofarden.townships.api.events;

import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.regions.Region;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CitizenExitRegionEvent extends Event {
    public CitizenExitRegionEvent(Citizen citizen, Region region) {
    }

    @Override public HandlerList getHandlers() {
        return null;
    }
}
