package net.kingdomsofarden.townships.tasks;

import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.util.StoredDataSection;
import org.bukkit.Material;

import java.util.Map;

public class RegionBlockCheckTask implements Runnable {

    Map<Material, Integer> reqs;

    public RegionBlockCheckTask(Region region){
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
    }

    @Override
    public void run() {

    }
}
