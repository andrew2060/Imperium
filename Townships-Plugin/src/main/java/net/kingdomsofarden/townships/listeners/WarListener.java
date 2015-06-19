package net.kingdomsofarden.townships.listeners;

import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.events.CitizenEnterRegionEvent;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.util.Constants;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class WarListener implements Listener {
    // TODO 2.0 Dynamically bound regions
    @EventHandler(priority = EventPriority.LOWEST) public void onWarDeath(PlayerDeathEvent event) {
        Citizen dead = Townships.getCitizens().getCitizen(event.getEntity().getUniqueId());
        Region defendingRegion = dead.getCitizenRegion();
        if (event.getEntity().getKiller() != null) {
            defendingRegion.updatePower(-1 * Constants.POWER_LOSS_PER_DEATH_PVP);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEnterRegion(CitizenEnterRegionEvent event) {
        event.getCitizen()
    }
}
