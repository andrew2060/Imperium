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
        Townships.getRegions().getBoundingRegions(loc).stream()
            .filter(r -> r.getMetadata().containsKey(MetaKeys.REQUIREMENT_BLOCK)).forEach(r -> {
            RegionBlockCheckTask task =
                (RegionBlockCheckTask) r.getMetadata().get(MetaKeys.REQUIREMENT_BLOCK);
            task.schedule(event.getBlock().getType());
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRegionDisband(RegionDisbandEvent event) {
        Townships.getRegions().getIntersectingRegions(event.getRegion().getBounds()).stream()
            .filter(r -> r.getMetadata().containsKey(MetaKeys.REQUIREMENT_REGION)).forEach(r -> {
            RegionSubregionCheckTask task =
                (RegionSubregionCheckTask) r.getMetadata().get(MetaKeys.REQUIREMENT_REGION);
            task.schedule(event.getRegion().getType(), event.getRegion().getTier());
        });
    }
}
