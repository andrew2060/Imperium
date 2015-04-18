package net.kingdomsofarden.townships.tasks;

import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.api.events.RegionDisbandEvent;
import net.kingdomsofarden.townships.api.events.RegionDisbandEvent.DisbandCause;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.util.StoredDataSection;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.Map;

public class RegionBlockCheckTask implements Runnable {

    private Region region;
    private Map<Material, Integer> reqs;

    public RegionBlockCheckTask(Region region) {
        StoredDataSection data = Townships.getConfiguration().getRegionConfiguration(region.getType()).orNull();
        if (data == null) {
            throw new IllegalStateException("Supplied region does not have a corresponding type configuration!");
        }
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
    }

    @Override
    public void run() {
        if (region.isValid()) { // If invalid, assume pending removal anyways
            Map<Material, Integer> remaining = region.getBounds().checkForBlocks(reqs);
            if (!remaining.isEmpty()) {
                // Trigger a region disband
                region.setValid(false); // Indicate this region is pending deletion and no longer valid/should not be checked again
                RegionDisbandEvent event = new RegionDisbandEvent(region, DisbandCause.BLOCK_REQUIREMENTS_NOT_MET);
                Bukkit.getPluginManager().callEvent(event);
                Townships.getRegions().remove(region); // We ignore cancellation state for requirement failures
            }
        }
    }
}
