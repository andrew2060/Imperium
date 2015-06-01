package net.kingdomsofarden.townships.effects.core;

import net.kingdomsofarden.townships.api.ITownshipsPlugin;
import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.effects.Effect;
import net.kingdomsofarden.townships.api.events.EconomyTransactionEvent.TransactionType;
import net.kingdomsofarden.townships.api.events.PlayerEconomyTransactionEvent;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.resources.EconomyProvider;
import net.kingdomsofarden.townships.api.util.StoredDataSection;
import net.kingdomsofarden.townships.resources.VaultEconomyProvider;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class EffectIncomeTax implements Effect, Listener {
    private Region region;
    private double tax;

    @Override
    public String getName() {
        return "income-tax";
    }

    @Override
    public void onInit(ITownshipsPlugin plugin) {

    }

    @Override
    public void onLoad(ITownshipsPlugin plugin, Region region, StoredDataSection data) {
        this.region = region;
        tax = 0;
        try {
            tax = Double.valueOf(data.get("rate", "0.00"));
        } catch (NumberFormatException e) {
            tax = 0;
        }
        if (!this.region.getEconomyProviders().containsKey(EconomyProvider.TREASURY)) {
            this.region.getEconomyProviders().put(EconomyProvider.TREASURY, new VaultEconomyProvider(region.getUid(), region, EconomyProvider.TREASURY));
        }
        Bukkit.getPluginManager().registerEvents(this, (Plugin) plugin.getBackingImplementation());
    }

    @Override
    public void onUnload(ITownshipsPlugin plugin, Region region, StoredDataSection data) {
        data.set("rate", tax);
        PlayerEconomyTransactionEvent.getHandlerList().unregister(this);
    }

    @Override
    public Region getRegion() {
        return region;
    }

    @EventHandler(priority= EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerDepositTransaction(PlayerEconomyTransactionEvent event) {
        if (event.getType().equals(TransactionType.DEPOSIT)) {
            Citizen c = Townships.getCitizens().getCitizen(event.getPlayer().getUniqueId());
            if (region.isCitizen(c)) {
                double amount = event.getAmount();
                double taxed = amount * tax;
                amount -= taxed;
                event.setAmount(amount);
                region.getEconomyProviders().get(EconomyProvider.TREASURY).deposit(amount);
            }
        }
    }

    public double getAmount() {
        return tax;
    }
}
