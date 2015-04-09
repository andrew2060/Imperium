package net.kingdomsofarden.townships.effects.core;

import net.kingdomsofarden.townships.api.ITownshipsPlugin;
import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.api.events.RegionDisbandEvent;
import net.kingdomsofarden.townships.api.events.RegionDisbandEvent.DisbandCause;
import net.kingdomsofarden.townships.api.resources.EconomyProvider;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.resources.ItemProvider;
import net.kingdomsofarden.townships.api.util.Serializer;
import net.kingdomsofarden.townships.api.util.StoredDataSection;
import net.kingdomsofarden.townships.effects.common.EffectPeriodic;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class EffectUpkeep extends EffectPeriodic {


    private Double cost;
    private Map<Material, Integer> resources;

    @Override
    public long onTick(Region region, long time) {
        Collection<EconomyProvider> econProviders = region.getEconomyProviders();
        if (cost > 0) {
            double amt = cost;
            for (EconomyProvider provider : econProviders) {
                amt -= provider.getBalance();
                if (amt <= 0) {
                    break;
                }
            }
            if (amt > 0) {
                RegionDisbandEvent event = new RegionDisbandEvent(region, DisbandCause.UPKEEP);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    Townships.getRegions().remove(region);
                }
                return Long.MAX_VALUE; // Safety in case not properly removed
            } else {
                amt = cost;
                for (EconomyProvider provider : econProviders) {
                    double bal = provider.getBalance();
                    double withdraw;
                    if (bal >= amt) {
                        withdraw = amt;
                    } else {
                        withdraw = bal;
                    }
                    provider.withdraw(withdraw);
                    amt -= withdraw;
                    if (amt <= 0) {
                        break;
                    }
                }
            }
        }
        Collection<ItemProvider> itemProviders = region.getItemProviders();

        if (!resources.isEmpty()) {
            boolean hasAll = true;
            for (Entry<Material, Integer> entry : resources.entrySet()) {
                int amt = entry.getValue();
                for (ItemProvider provider : itemProviders) {
                    amt -= provider.getAmount(entry.getKey());
                    if (amt <= 0) {
                        break;
                    }
                }
                if (amt > 0) {
                    hasAll = false;
                    break;
                }
            }
            if (!hasAll) {
                RegionDisbandEvent event = new RegionDisbandEvent(region, DisbandCause.UPKEEP);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    Townships.getRegions().remove(region);
                    return Long.MAX_VALUE;
                } else {
                    return super.onTick(region, time);
                }
            } else {
                for (Entry<Material, Integer> entry : resources.entrySet()) {
                    int amt = entry.getValue();
                    for (ItemProvider provider : itemProviders) {
                        amt -= provider.remove(entry.getKey(), amt);
                        if (amt <= 0) {
                            break;
                        }
                    }
                }
                return super.onTick(region, time);
            }
        } else {
            return super.onTick(region, time);
        }
    }

    @Override
    public String getName() {
        return "upkeep";
    }

    @Override
    public void onLoad(ITownshipsPlugin plugin, Region r, StoredDataSection data) {
        region = r;
        resources = new HashMap<Material, Integer>();
        for (String entry : data.getList("resources")) {
            String[] parse = entry.split(" ");
            try {
                Material mat = Material.matchMaterial(parse[0]);
                int count = Integer.valueOf(parse[1]);
                resources.put(mat, count);
            } catch (Exception e) {
                // TODO debug
            }
        }
        cost = data.get("money", new Serializer<Double>() {
            @Override
            public String serialize(Double obj) {
                return obj + "";
            }

            @Override
            public Double deserialize(String input) {
                return Double.valueOf(input);
            }
        }, 0.00);
    }

    @Override
    public void onUnload(ITownshipsPlugin plugin, Region region, StoredDataSection data) {
        if (cost != 0.00) {
            data.set("money", cost);
        }
        if (!resources.isEmpty()) {
            List<String> save = new LinkedList<String>();
            for (Entry<Material, Integer> e : resources.entrySet()) {
                save.add(e.getKey().name() + " " + e.getValue());
            }
            data.set("resources", save);
        }
    }

    @Override
    public Region getRegion() {
        return region;
    }
}
