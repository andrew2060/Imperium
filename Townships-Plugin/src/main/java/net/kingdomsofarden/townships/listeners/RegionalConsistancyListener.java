package net.kingdomsofarden.townships.listeners;

import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.api.events.RegionCreateEvent;
import net.kingdomsofarden.townships.api.events.RegionDisbandEvent;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.regions.bounds.RegionBoundingArea;
import net.kingdomsofarden.townships.regions.bounds.RegionDynamicCompositeBoundingArea;
import net.kingdomsofarden.townships.tasks.RegionBlockCheckTask;
import net.kingdomsofarden.townships.tasks.RegionSubregionCheckTask;
import net.kingdomsofarden.townships.util.MetaKeys;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Collection;
import java.util.TreeSet;

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
        // Trigger region disbandents
        TreeSet<Region> intersections =
            Townships.getRegions().getIntersectingRegions(event.getRegion().getBounds());
        intersections.stream().filter(r -> r.getMetadata().containsKey(MetaKeys.REQUIREMENT_REGION))
            .forEach(r -> {
                RegionSubregionCheckTask task =
                    (RegionSubregionCheckTask) r.getMetadata().get(MetaKeys.REQUIREMENT_REGION);
                task.schedule(event.getRegion().getType(), event.getRegion().getTier());
            });
        // Remove from any intersecting towns
        intersections.stream()
            .filter(r -> r.getBounds() instanceof RegionDynamicCompositeBoundingArea).forEach(
            r -> ((RegionDynamicCompositeBoundingArea) r.getBounds()).remove(event.getRegion()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRegionCreate(RegionCreateEvent event) {
        // Add newly created regions to intersecting dynamic bounds if necessary
        Collection<RegionBoundingArea> intersect =
            Townships.getRegions().getIntersectingBounds(event.getRegion().getBounds());
        final RegionBoundingArea[] part = {null};
        final double[] power = {0};
        intersect.stream().filter(b -> b instanceof RegionDynamicCompositeBoundingArea)
            .forEach(b -> {
                // A memory check suffices
                intersect.stream().filter(region -> region != b)
                    .forEach(region -> { // A memory check suffices
                        double currPower =
                            (double) region.getRegion().getRegionalMetadata(b.getRegion())
                                .getOrDefault(MetaKeys.MAX_POWER, 0);
                        if (currPower > power[0]) {
                            part[0] = b;
                            power[0] = currPower;
                        }
                    });
            });
        if (part[0] != null
            && power[0] - ((RegionDynamicCompositeBoundingArea) part[0]).getDecay() > 0) {
            ((RegionDynamicCompositeBoundingArea) part[0]).add(event.getRegion());
            event.getRegion().getRegionalMetadata(part[0].getRegion()).put(MetaKeys.MAX_POWER,
                power[0] - ((RegionDynamicCompositeBoundingArea) part[0]).getDecay());
        }
    }
}
