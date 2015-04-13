package net.kingdomsofarden.townships.resources;

import net.kingdomsofarden.townships.api.resources.ItemProvider;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class PhysicalChestItemProvider implements ItemProvider {

    protected Location chestLocation;
    protected int priority;

    @Override
    public int getAmount(Material mat) {
        if (chestLocation == null) {
            return 0;
        }
        Block b = chestLocation.getBlock();
        Chest chest;
        if (b.getType() == Material.CHEST) {
            chest = (Chest) b.getState();
        } else {
            return 0;
        }
        int amt = 0;
        for (Entry<Integer, ? extends ItemStack> entry : chest.getBlockInventory().all(mat).entrySet()) {
             amt += entry.getValue().getAmount();
        }
        return amt;
    }

    @Override
    public int remove(Material mat, int max) {
        if (chestLocation == null) {
            return 0;
        }
        Block b = chestLocation.getBlock();
        Chest chest;
        if (b.getType() == Material.CHEST) {
            chest = (Chest) b.getState();
        } else {
            return 0;
        }
        int amt = max;
        Map<Integer, Integer> slotRemoveAmts = new HashMap<Integer, Integer>();
        for (Entry<Integer, ? extends ItemStack> entry : chest.getBlockInventory().all(mat).entrySet()) {
            int removable = entry.getValue().getAmount();
            removable = removable > amt ? amt : removable;
            slotRemoveAmts.put(entry.getKey(), removable);
            amt -= removable;
        }
        Inventory inv = chest.getBlockInventory();
        for (Entry<Integer, Integer> entry : slotRemoveAmts.entrySet()) {
            ItemStack i = inv.getItem(entry.getKey());
            if (i.getAmount() == entry.getValue()) {
                inv.clear(entry.getKey());
            } else {
                i.setAmount(entry.getValue());
            }
        }
        return max - amt;
    }


    @Override
    public int add(Material type, int max) {
        if (chestLocation == null) {
            return 0;
        }
        Block b = chestLocation.getBlock();
        Chest chest;
        if (b.getType() == Material.CHEST) {
            chest = (Chest) b.getState();
        } else {
            return 0;
        }
        Inventory inv = chest.getBlockInventory();
        int rem = 0;
        for (Entry<Integer, ItemStack> remaining : inv.addItem(new ItemStack(type, max)).entrySet()) {
            rem += remaining.getValue().getAmount();
        }
        return max - rem;
    }

    @Override
    public int getPriority() {
        return priority;
    }

}
