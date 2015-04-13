package net.kingdomsofarden.townships.effects.denyexplosions.listener;

import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.api.regions.Region;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class ExplosiveProtectionListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        for (Region r : Townships.getRegions().getBoundingRegions(event.getLocation())) {
            if (r.hasEffect("deny-explosions")) {
                event.setYield(0.0F);
            }
        }
    }

}
