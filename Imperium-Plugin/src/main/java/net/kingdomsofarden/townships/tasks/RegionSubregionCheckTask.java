package net.kingdomsofarden.townships.tasks;

import net.kingdomsofarden.townships.TownshipsPlugin;
import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.api.events.RegionDisbandEvent;
import net.kingdomsofarden.townships.api.events.RegionDisbandEvent.DisbandCause;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.util.StoredDataSection;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class RegionSubregionCheckTask implements Runnable {

    private Region region;
    private HashMap<Integer, Integer> regionTierMinReq;
    private HashMap<String, Integer> regionTypeMinReq;
    private Set<Integer> checkedTiers;
    private Set<String> checkedTypes;
    private boolean scheduled;
    private TownshipsPlugin plugin;

    public RegionSubregionCheckTask(Region region, TownshipsPlugin plugin) {
        this.plugin = plugin;
        StoredDataSection data =
            Townships.getConfiguration().getRegionConfiguration(region.getType()).orNull();
        if (data == null) {
            throw new IllegalStateException(
                "Supplied region does not have a corresponding type configuration!");
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
        this.scheduled = false;
        this.checkedTiers = new LinkedHashSet<Integer>();
        this.checkedTypes = new LinkedHashSet<String>();
    }

    @Override public void run() {
        scheduled = false;
        if (region.isValid()) {
            HashMap<String, Integer> typeReq = new HashMap<String, Integer>();
            HashMap<Integer, Integer> tierReq = new HashMap<Integer, Integer>();
            for (int i : checkedTiers) {
                tierReq.put(i, regionTierMinReq.get(i));
            }
            checkedTiers.clear();
            for (String t : checkedTypes) {
                typeReq.put(t, regionTypeMinReq.get(t));
            }
            checkedTypes.clear();
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
                RegionDisbandEvent event =
                    new RegionDisbandEvent(region, DisbandCause.BLOCK_REQUIREMENTS_NOT_MET);
                Bukkit.getPluginManager().callEvent(event);
            }
        }
    }


    public void schedule(String type, int tier) {
        boolean schedule = false;
        if (regionTierMinReq.containsKey(tier)) {
            schedule = true;
            checkedTiers.add(tier);
        }
        if (regionTypeMinReq.containsKey(type)) {
            schedule = true;
            checkedTypes.add(type);
        }
        if (schedule && !scheduled) {
            scheduled = true;
            Bukkit.getScheduler().runTask(plugin, this);
        }
    }
}
