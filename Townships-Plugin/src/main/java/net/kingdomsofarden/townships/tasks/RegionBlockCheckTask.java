package net.kingdomsofarden.townships.tasks;

import net.kingdomsofarden.townships.TownshipsPlugin;
import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.api.events.RegionDisbandEvent;
import net.kingdomsofarden.townships.api.events.RegionDisbandEvent.DisbandCause;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.util.StoredDataSection;
import net.kingdomsofarden.townships.util.Constants;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class RegionBlockCheckTask implements Runnable {



    private TownshipsPlugin plugin;
    private boolean scheduled;

    private Region region;
    private Map<Material, Integer> reqs;

    private Set<Material> processQueue;

    public RegionBlockCheckTask(Region region, TownshipsPlugin plugin) {
        this.plugin = plugin;
        StoredDataSection data =
            Townships.getConfiguration().getRegionConfiguration(region.getType()).orNull();
        if (data == null) {
            throw new IllegalStateException(
                "Supplied region does not have a corresponding type configuration!");
        }
        reqs = new HashMap<Material, Integer>();
        StoredDataSection requirements = data.getSection("requirements");
        StoredDataSection blockReqSection = requirements.getSection("block-requirements");
        for (String matName : blockReqSection.getKeys(false)) {
            Material mat = Material.valueOf(matName.toUpperCase());
            if (mat == null) {
                // TODO error in console
                continue;
            }
            int amt = Integer.valueOf(blockReqSection.get(matName, "0"));
            if (amt > 0) {
                reqs.put(mat, amt);
            }
        }
        this.region = region;
        this.processQueue = new LinkedHashSet<Material>();
    }

    @Override public void run() {
        scheduled = false;
        if (region.isValid()) { // If invalid, assume pending removal anyways
            Map<Material, Integer> amounts = new HashMap<Material, Integer>();
            for (Material mat : processQueue) {
                amounts.put(mat, reqs.get(mat));
            }
            processQueue.clear();
            Map<Material, Integer> remaining = region.getBounds().checkForBlocks(amounts);
            if (!remaining.isEmpty()) {
                // Trigger a region disband
                Townships.getRegions()
                    .remove(region); // We ignore cancellation state for requirement failures
                RegionDisbandEvent event =
                    new RegionDisbandEvent(region, DisbandCause.BLOCK_REQUIREMENTS_NOT_MET);
                Bukkit.getPluginManager().callEvent(event);
            }
        }
    }

    public void schedule(Material mat) {
        if (reqs.containsKey(mat)) {
            processQueue.add(mat);
            if (!scheduled) {
                scheduled = true;
                Bukkit.getScheduler().runTaskLater(plugin, this, Constants.BLOCK_CHECK_DELAY);
            }
        }
    }
}
