package net.kingdomsofarden.townships.effects.core;

import net.kingdomsofarden.townships.api.ITownshipsPlugin;
import net.kingdomsofarden.townships.api.events.ProductionEvent;
import net.kingdomsofarden.townships.api.regions.FunctionalRegion;
import net.kingdomsofarden.townships.api.resources.EconomyProvider;
import net.kingdomsofarden.townships.api.resources.ItemProvider;
import net.kingdomsofarden.townships.api.util.StoredDataSection;
import net.kingdomsofarden.townships.effects.common.EffectPeriodicCost;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class EffectProduction extends EffectPeriodicCost {

    private HashMap<String, Integer> production;
    private double econProduction;

    @Override protected boolean onSuccessfulTick(FunctionalRegion region) {
        HashMap<String, Integer> toEvent = new HashMap<>(production);
        ProductionEvent e = new ProductionEvent(toEvent, econProduction);
        Bukkit.getPluginManager().callEvent(e);
        if (e.isCancelled()) {
            return true;
        }
        Map<String, Integer> result = e.getItemProduction();
        if (result.size() > 0) {
            for (Entry<String, Integer> entry : result.entrySet()) {
                int amt = entry.getValue();
                for (ItemProvider provider : region.getItemProviders().values()) {
                    amt -= provider.add(Material.valueOf(entry.getKey()), amt);
                    if (amt == 0) {
                        break;
                    }
                }
            }
        }
        double deposit = e.getEconProduction();
        if (deposit > 0) {
            for (EconomyProvider provider : region.getEconomyProviders().values()) {
                deposit -= provider.deposit(deposit);
                if (deposit == 0.00) {
                    break;
                }
            }
        }
        return true;
    }

    @Override protected boolean onInsufficientResources(FunctionalRegion region) {
        return true;
    }

    @Override public String getName() {
        return "production";
    }

    @Override public void onInit(ITownshipsPlugin plugin) {
    }

    @Override public void onLoad(ITownshipsPlugin plugin, FunctionalRegion r, StoredDataSection data) {
        super.onLoad(plugin, r, data);
        production = new HashMap<>();
        StoredDataSection produce = data.getSection("produce");
        for (String entry : produce.getList("resources")) {
            String[] parse = entry.split(" ");
            Material type = Material.matchMaterial(parse[0]);
            Integer amount = Integer.valueOf(parse[1]);
            production.put(type.toString(), amount);
        }
        econProduction = Double.valueOf(produce.get("money", "0"));
    }

    @Override public void onUnload(ITownshipsPlugin plugin, FunctionalRegion region, StoredDataSection data) {
        super.onUnload(plugin, region, data);
        StoredDataSection subSection = data.getSection("produce");
        subSection.set("money", econProduction);
        List<String> store =
            production.entrySet().stream().map(e -> e.getKey() + " " + e.getValue())
                .collect(Collectors.toCollection(LinkedList::new));
        subSection.set("resources", store);
    }
}
