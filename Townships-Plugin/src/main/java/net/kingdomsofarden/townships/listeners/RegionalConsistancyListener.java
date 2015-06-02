package net.kingdomsofarden.townships.listeners;

import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.api.events.RegionDisbandEvent;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.tasks.RegionBlockCheckTask;
import net.kingdomsofarden.townships.tasks.RegionSubregionCheckTask;
import net.kingdomsofarden.townships.util.MetaKeys;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class RegionalConsistancyListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Location loc = event.getBlock().getLocation();
        for (Region r : Townships.getRegions().getBoundingRegions(loc)) {
            if (r.getMetadata().containsKey(MetaKeys.REQUIREMENT_BLOCK)) {
                RegionBlockCheckTask task =
                    (RegionBlockCheckTask) r.getMetadata().get(MetaKeys.REQUIREMENT_BLOCK);
                task.schedule(event.getBlock().getType());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRegionDisband(RegionDisbandEvent event) {
        for (Region r : Townships.getRegions()
            .getIntersectingRegions(event.getRegion().getBounds())) {
            if (r.getMetadata().containsKey(MetaKeys.REQUIREMENT_REGION)) {
                RegionSubregionCheckTask task =
                    (RegionSubregionCheckTask) r.getMetadata().get(MetaKeys.REQUIREMENT_REGION);
                task.schedule(event.getRegion().getType(), event.getRegion().getTier());
            }
        }
    }
}
