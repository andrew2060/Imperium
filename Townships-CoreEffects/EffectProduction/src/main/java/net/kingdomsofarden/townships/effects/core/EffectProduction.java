package net.kingdomsofarden.townships.effects.core;

import net.kingdomsofarden.townships.api.ITownshipsPlugin;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.resources.EconomyProvider;
import net.kingdomsofarden.townships.api.resources.ItemProvider;
import net.kingdomsofarden.townships.api.util.StoredDataSection;
import net.kingdomsofarden.townships.effects.common.EffectPeriodicCost;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map.Entry;

public class EffectProduction extends EffectPeriodicCost {

    private HashMap<Material, Integer> production;
    private double econProduction;

    @Override
    protected boolean onSuccessfulTick(Region region) {
        for (Entry<Material, Integer> entry : production.entrySet()) {
            int amt = entry.getValue();
            for (ItemProvider provider : region.getItemProviders()) {
                amt -= provider.add(entry.getKey(), amt);
                if (amt == 0) {
                    break;
                }
            }
        }
        double deposit = econProduction;
        for (EconomyProvider provider : region.getEconomyProviders()) {
            deposit -= provider.deposit(deposit);
            if (deposit == 0.00) {
                break;
            }
        }
        return true;
    }

    @Override
    protected boolean onInsufficientResources(Region region) {
        return true;
    }

    @Override
    public String getName() {
        return "production";
    }

    @Override
    public void onInit(ITownshipsPlugin plugin) {}

    @Override
    public void onLoad(ITownshipsPlugin plugin, Region r, StoredDataSection data) {
        super.onLoad(plugin, r, data);
        production = new HashMap<Material, Integer>();
        StoredDataSection produce = data.getSection("produce");
        for (String entry : produce.getList("resources")) {
            String[] parse = entry.split(" ");
            Material type = Material.matchMaterial(parse[0]);
            Integer amount = Integer.valueOf(parse[1]);
            production.put(type, amount);
        }
        econProduction = Double.valueOf(produce.get("money", "0"));
    }
}
