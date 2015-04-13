package net.kingdomsofarden.townships.effects.upkeep;

import net.kingdomsofarden.townships.api.ITownshipsPlugin;
import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.api.events.RegionDisbandEvent;
import net.kingdomsofarden.townships.api.events.RegionDisbandEvent.DisbandCause;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.effects.common.EffectPeriodicCost;
import org.bukkit.Bukkit;

public class EffectUpkeep extends EffectPeriodicCost {
    @Override
    protected boolean onSuccessfulTick(Region region) {
        return true;
    }

    @Override
    protected boolean onInsufficientResources(Region region) {
        RegionDisbandEvent event = new RegionDisbandEvent(region, DisbandCause.UPKEEP);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            Townships.getRegions().remove(region);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String getName() {
        return "upkeep";
    }

    @Override
    public void onInit(ITownshipsPlugin plugin) {

    }
}
