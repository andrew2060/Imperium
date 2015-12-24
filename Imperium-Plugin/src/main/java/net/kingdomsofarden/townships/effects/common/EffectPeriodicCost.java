package net.kingdomsofarden.townships.effects.common;

import net.kingdomsofarden.townships.api.ITownshipsPlugin;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.resources.EconomyProvider;
import net.kingdomsofarden.townships.api.resources.ItemProvider;
import net.kingdomsofarden.townships.api.util.Serializer;
import net.kingdomsofarden.townships.api.util.StoredDataSection;
import org.bukkit.Material;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public abstract class EffectPeriodicCost extends EffectPeriodic {


    private Double cost;
    private Map<Material, Integer> resources;

    @Override public long onTick(Region region, long time) {
        Collection<EconomyProvider> econProviders = region.getEconomyProviders().values();
        if (cost > 0) {
            double amt = cost;
            for (EconomyProvider provider : econProviders) {
                amt -= provider.getBalance();
                if (amt <= 0) {
                    break;
                }
            }
            if (amt > 0) {
                if (onInsufficientResources(region)) {
                    return super.onTick(region, time);
                } else {
                    return Long.MAX_VALUE;
                }
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
        Collection<ItemProvider> itemProviders = region.getItemProviders().values();
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
                if (onInsufficientResources(region)) {
                    return super.onTick(region, time);
                } else {
                    return Long.MAX_VALUE;
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
        }
        if (this.onSuccessfulTick(region)) {
            return super.onTick(region, time);
        } else {
            return Long.MAX_VALUE;
        }
    }

    /**
     * Runs when tick is run successfully
     *
     * @param region The region to be ticked
     * @return Whether a repeat should be scheduled
     */
    protected abstract boolean onSuccessfulTick(Region region);

    /**
     * Runs when tick is run unsuccessfully (i.e. upkeep requirements not met)
     *
     * @param region The region being ticked
     * @return Whether the region should be ticked again
     */
    protected abstract boolean onInsufficientResources(Region region);

    @Override public void onLoad(ITownshipsPlugin plugin, Region r, StoredDataSection data) {
        super.onLoad(plugin, r, data);
        StoredDataSection subSection = data.getSection("cost");
        resources = new HashMap<>();
        for (String entry : subSection.getList("resources")) {
            String[] parse = entry.split(" ");
            try {
                Material mat = Material.matchMaterial(parse[0]);
                int count = Integer.valueOf(parse[1]);
                resources.put(mat, count);
            } catch (Exception e) {
                // TODO debug
            }
        }
        cost = subSection.get("money", new Serializer<Double>() {
            @Override public String serialize(Double obj) {
                return obj + "";
            }

            @Override public Double deserialize(String input) {
                return Double.valueOf(input);
            }
        }, 0.00);
    }

    @Override public void onUnload(ITownshipsPlugin plugin, Region region, StoredDataSection data) {
        super.onUnload(plugin, region, data);
        StoredDataSection subSection = data.getSection("cost");
        if (cost != 0.00) {
            subSection.set("money", cost);
        }
        if (!resources.isEmpty()) {
            List<String> save =
                resources.entrySet().stream().map(e -> e.getKey().name() + " " + e.getValue())
                    .collect(Collectors.toCollection(LinkedList::new));
            subSection.set("resources", save);
        }
    }

    @Override public Region getRegion() {
        return region;
    }
}
