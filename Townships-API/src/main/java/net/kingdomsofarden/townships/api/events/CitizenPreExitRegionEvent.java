package net.kingdomsofarden.townships.api.events;

import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.regions.Region;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CitizenPreExitRegionEvent extends Event implements Cancellable{
    public CitizenPreExitRegionEvent(Citizen c, Region r) {

    }

    @Override public boolean isCancelled() {
        return false;
    }

    @Override public void setCancelled(boolean b) {

    }

    @Override public HandlerList getHandlers() {
        return null;
    }
}
