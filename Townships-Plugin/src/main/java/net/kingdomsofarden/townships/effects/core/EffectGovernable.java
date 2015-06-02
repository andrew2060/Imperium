package net.kingdomsofarden.townships.effects.core;

import net.kingdomsofarden.townships.api.ITownshipsPlugin;
import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.events.BankEconomyTransactionEvent;
import net.kingdomsofarden.townships.api.events.EconomyTransactionEvent.TransactionType;
import net.kingdomsofarden.townships.api.events.PlayerEconomyTransactionEvent;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.resources.EconomyProvider;
import net.kingdomsofarden.townships.api.util.StoredDataSection;
import net.kingdomsofarden.townships.effects.common.EffectPeriodic;
import net.kingdomsofarden.townships.resources.VaultEconomyProvider;
import net.kingdomsofarden.townships.util.Constants;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class EffectGovernable extends EffectPeriodic implements Listener {



    Stats curr;
    Stats last;

    @Override public String getName() {
        return "governable";
    }

    public int getCurrentPop() {
        return curr.pop;
    }

    public int getCurrLand() {
        return curr.land;
    }

    public double getProd() {
        long prop = System.currentTimeMillis() % Constants.YEAR_LENGTH;
        double ratio = ((double) prop) / Constants.YEAR_LENGTH;
        return curr.prod + last.prod * (1 - ratio);
    }

    public double getGnp() {
        long rem = System.currentTimeMillis() % Constants.YEAR_LENGTH;
        double ratio = ((double) rem) / Constants.YEAR_LENGTH;
        return curr.gnp + last.gnp * (1 - ratio);
    }

    public double getExp() {
        long rem = System.currentTimeMillis() % Constants.YEAR_LENGTH;
        double ratio = ((double) rem) / Constants.YEAR_LENGTH;
        return curr.exp + last.exp * (1 - ratio);
    }

    public double getNNI() {
        return getGnp() - getExp();
    }

    @Override public void onInit(ITownshipsPlugin plugin) {

    }

    @Override public long onTick(Region region, long time) {
        updateImmed();
        last = curr;
        curr = new Stats();
        curr.pop = last.pop;
        curr.land = last.land;
        return super.onTick(region, time);
    }

    @Override public void onLoad(ITownshipsPlugin plugin, Region r, StoredDataSection data) {
        super.onLoad(plugin, r, data);
        if (startTime == -1) {
            startTime = System.currentTimeMillis() / Constants.YEAR_LENGTH + Constants.YEAR_LENGTH;
        }
        period = Constants.YEAR_LENGTH;
        last = new Stats();
        last.pop = Integer.valueOf(data.get("last.pop", "0"));
        last.land = Integer.valueOf(data.get("last.land", "0"));
        last.prod = Integer.valueOf(data.get("last.prod", "0"));
        last.gnp = Double.valueOf(data.get("last.gnp", "0"));
        last.exp = Double.valueOf(data.get("last.exp", "0"));
        curr = new Stats();
        curr.pop = Integer.valueOf(data.get("curr.pop", "0"));
        curr.land = Integer.valueOf(data.get("curr.land", "0"));
        curr.prod = Integer.valueOf(data.get("curr.prod", "0"));
        curr.gnp = Double.valueOf(data.get("curr.gnp", "0"));
        curr.exp = Double.valueOf(data.get("curr.exp", "0"));
        if (!region.getEconomyProviders().containsKey(EconomyProvider.TREASURY)) {
            region.getEconomyProviders().put(EconomyProvider.TREASURY,
                new VaultEconomyProvider(region.getUid(), region, EconomyProvider.TREASURY));
        }
    }

    public void onUnload(ITownshipsPlugin plugin, Region region, StoredDataSection data) {
        data.set("last.pop", last.pop);
        data.set("last.land", last.land);
        data.set("last.prod", last.prod);
        data.set("last.gnp", last.gnp);
        data.set("last.exp", last.exp);
        data.set("curr.pop", curr.pop);
        data.set("curr.land", curr.land);
        data.set("curr.prod", curr.prod);
        data.set("curr.gnp", curr.gnp);
        data.set("curr.exp", curr.exp);
    }

    public void updateImmed() {
        curr.pop = region.getCitizens().size();
        curr.land = region.getBounds().size2d();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBankTransaction(BankEconomyTransactionEvent event) {
        if (event.getBankName().equals(region.getName().or(""))) {
            if (event.getType().equals(TransactionType.DEPOSIT)) {
                curr.gnp += event.getAmount();
            } else {
                curr.exp += event.getAmount();
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerTransaction(PlayerEconomyTransactionEvent event) {
        Citizen c = Townships.getCitizens().getCitizen(event.getPlayer().getUniqueId());
        if (region.isCitizen(c)) {
            if (event.getType().equals(TransactionType.DEPOSIT)) {
                curr.gnp += event.getAmount();
            } else {
                curr.exp += event.getAmount();
            }
        }
    }


    class Stats {
        private int pop = 0;
        private int land = 0;
        private int prod = 0; // TODO events for this

        private double gnp = 0;
        private double exp = 0;
    }
}
