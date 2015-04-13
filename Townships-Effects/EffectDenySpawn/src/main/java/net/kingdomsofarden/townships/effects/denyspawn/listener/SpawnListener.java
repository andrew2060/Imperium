package net.kingdomsofarden.townships.effects.denyspawn.listener;

import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.effects.denyspawn.EffectDenySpawn;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class SpawnListener implements Listener {

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntitySpawn(EntitySpawnEvent event) {
        Location loc = event.getLocation();
        EntityType type = event.getEntityType();
        for (Region r : Townships.getRegions().getBoundingRegions(loc)) {
            if (r.hasEffect("deny-spawn")) {
                EffectDenySpawn effect = r.getEffect("deny-spawn");
                if (effect.isDenied(type)) {
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }
}
