package net.kingdomsofarden.townships.tasks;

import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.api.events.RegionDisbandEvent;
import net.kingdomsofarden.townships.api.events.RegionDisbandEvent.DisbandCause;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.util.StoredDataSection;
import org.bukkit.Bukkit;

import java.util.HashMap;

public class RegionSubregionCheckTask implements Runnable {

    private Region region;
    private HashMap<Integer, Integer> regionTierMinReq;
    private HashMap<String, Integer> regionTypeMinReq;

    public RegionSubregionCheckTask(Region region) {
        StoredDataSection data = Townships.getConfiguration().getRegionConfiguration(region.getType()).orNull();
        if (data == null) {
            throw new IllegalStateException("Supplied region does not have a corresponding type configuration!");
        }
        StoredDataSection requirements = data.getSection("requirements");
        regionTypeMinReq = new HashMap<String, Integer>();
        regionTierMinReq = new HashMap<Integer, Integer>();
        StoredDataSection regionReqSection = requirements.getSection("region-types-min");
        for (String type : regionReqSection.getKeys(false)) {
            int amt = Integer.parseInt(regionReqSection.get(type, "0"));
            if (amt > 0) {
                regionTypeMinReq.put(type.toLowerCase(), amt);
            }
        }
        StoredDataSection tierReqSection = requirements.getSection("region-tiers-min");
        for (String tierNum : tierReqSection.getKeys(false)) {
            int tier = Integer.parseInt(tierNum);
            int arg = Integer.parseInt(regionReqSection.get(tierNum, "0"));
            regionTierMinReq.put(tier, arg);
        }
        this.region = region;
    }

    @Override
    public void run() {
        if (region.isValid()) {
            HashMap<String, Integer> typeReq = new HashMap<String, Integer>(regionTypeMinReq);
            HashMap<Integer, Integer> tierReq = new HashMap<Integer, Integer>(regionTierMinReq);
            for (Region child : region.getChildren()) {
                String type = child.getType().toLowerCase();
                int tier = child.getTier();
                if (typeReq.containsKey(type)) {
                    int amt = typeReq.get(type) - 1;
                    if (amt <= 0) {
                        typeReq.remove(type);
                    } else {
                        typeReq.put(type, amt);
                    }
                }
                if (tierReq.containsKey(tier)) {
                    int amt = typeReq.get(type) - 1;
                    if (amt <= 0) {
                        typeReq.remove(type);
                    } else {
                        typeReq.put(type, amt);
                    }
                }
            }
            if (!(typeReq.isEmpty() && tierReq.isEmpty())) {
                Townships.getRegions().remove(region); // Ignore cancellation
                RegionDisbandEvent event = new RegionDisbandEvent(region, DisbandCause.BLOCK_REQUIREMENTS_NOT_MET);
                Bukkit.getPluginManager().callEvent(event);
            }
        }
    }



}
