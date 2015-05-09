package net.kingdomsofarden.townships.effects.taxing;

import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.resources.EconomyProvider;
import net.kingdomsofarden.townships.api.resources.ItemProvider;
import net.kingdomsofarden.townships.api.util.Serializer;
import net.kingdomsofarden.townships.api.util.StoredDataSection;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class CompositeTaxItem implements TaxItem {

    private double amount;
    private Map<Material, Integer> items;

    public CompositeTaxItem(StoredDataSection section) {
        this.amount = section.get("money", new Serializer<Double>() {
            @Override
            public String serialize(Double obj) {
                return obj + "";
            }

            @Override
            public Double deserialize(String input) {
                return Double.valueOf(input);
            }
        }, 0.00);
        this.items = new HashMap<Material, Integer>();
        StoredDataSection item = section.getSection("items");
        for (String key : item.getKeys(false)) {
            int amt = Integer.parseInt(item.get(key, "0"));
            Material type = Material.valueOf(key.toUpperCase());
            if (type != null && amt > 0) {
                items.put(type, amt);
            }
        }
    }

    public CompositeTaxItem() {
        amount = 0.00;
        items = new HashMap<Material, Integer>();
    }


    @Override
    public boolean tax(Region taxer, Region taxed) {
        EconomyProvider selectedE = null;
        for (EconomyProvider provider : taxed.getEconomyProviders()) {
            if (provider.getIdentifier().equals("tax-account")) {
                if (provider.getBalance() < amount) {
                    return false;
                } else {
                    selectedE = provider;
                    break;
                }
            }
        }
        ItemProvider selectedI = null;
        for (ItemProvider provider : taxed.getItemProviders()) {
            if (provider.getIdentifier().equals("tax-account")) {
                for (Entry<Material, Integer> e : items.entrySet()) {
                    if (provider.getAmount(e.getKey()) < e.getValue()) {
                        return false;
                    }
                }
                selectedI = provider;
                break;
            }
        }

        if (selectedE != null) {
            selectedE.withdraw(amount);
        }
        if (selectedI != null) {
            for (Entry<Material, Integer> e : items.entrySet()) {
                selectedI.remove(e.getKey(), e.getValue());
            }
        }
        return true;
    }

    @Override
    public void registerOutstanding(TaxItem tax, Region taxed) {
        for (EconomyProvider provider : taxed.getEconomyProviders()) {
            if (provider.getIdentifier().equals("tax-account")) {
                double bal = provider.getBalance();
                amount += tax.getAmount() - bal;
                if (bal > 0) {
                    provider.withdraw(bal);
                }
                break;
            }
        }
        for (ItemProvider provider : taxed.getItemProviders()) {
            if (provider.getIdentifier().equals("tax-account")) {
                for (Entry<Material, Integer> e : tax.getItems().entrySet()) {
                    Material key = e.getKey();
                    int amount = provider.getAmount(key);
                    int rem = e.getValue() - amount;
                    provider.remove(key, amount);
                    items.put(key, items.getOrDefault(key, 0) + rem);
                }
                break;
            }
        }
    }

    @Override
    public double getAmount() {
        return amount;
    }

    @Override
    public Map<Material, Integer> getItems() {
        return items;
    }
}
