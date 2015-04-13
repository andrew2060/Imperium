package net.kingdomsofarden.townships.resources;

import net.kingdomsofarden.townships.api.resources.ItemProvider;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class VirtualChestItemProvider implements ItemProvider {

    protected Inventory inventory;
    protected int priority;

    @Override
    public int getAmount(Material mat) {
        if (inventory == null) {
            return 0;
        }
        int amt = 0;
        for (Entry<Integer, ? extends ItemStack> entry : inventory.all(mat).entrySet()) {
            amt += entry.getValue().getAmount();
        }
        return amt;
    }

    @Override
    public int remove(Material mat, int max) {
        if (inventory == null) {
            return 0;
        }
        int amt = max;
        Map<Integer, Integer> slotRemoveAmts = new HashMap<Integer, Integer>();
        for (Entry<Integer, ? extends ItemStack> entry : inventory.all(mat).entrySet()) {
            int removable = entry.getValue().getAmount();
            removable = removable > amt ? amt : removable;
            slotRemoveAmts.put(entry.getKey(), removable);
            amt -= removable;
        }
        for (Entry<Integer, Integer> entry : slotRemoveAmts.entrySet()) {
            ItemStack i = inventory.getItem(entry.getKey());
            if (i.getAmount() == entry.getValue()) {
                inventory.clear(entry.getKey());
            } else {
                i.setAmount(entry.getValue());
            }
        }
        return max - amt;
    }

    @Override
    public int add(Material type, int max) {
        if (inventory == null) {
            return 0;
        }
        int rem = 0;
        for (Entry<Integer, ItemStack> remaining : inventory.addItem(new ItemStack(type, max)).entrySet()) {
            rem += remaining.getValue().getAmount();
        }
        return max - rem;
    }

    @Override
    public int getPriority() {
        return priority;
    }
}
