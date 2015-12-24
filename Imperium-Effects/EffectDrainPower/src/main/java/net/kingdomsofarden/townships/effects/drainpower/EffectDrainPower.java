package net.kingdomsofarden.townships.effects.drainpower;

import net.kingdomsofarden.townships.api.ITownshipsPlugin;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.util.StoredDataSection;
import net.kingdomsofarden.townships.effects.common.EffectPeriodicCost;
import net.kingdomsofarden.townships.util.MetaKeys;

import java.util.Map;

public class EffectDrainPower extends EffectPeriodicCost {

    private int drainAmount;

    @Override protected boolean onSuccessfulTick(Region region) {
        Map<String, Object> meta = region.getMetadata();
        if (!meta.containsKey(MetaKeys.POWER)) { // Doesn't have power meta, don't reschedule
            return false;
        }
        int curr = (Integer) meta.get(MetaKeys.POWER);
        curr -= drainAmount; // TODO call event
        if (curr <= 0) {
            curr = 0;
        }
        meta.put(MetaKeys.POWER, curr);
        return true;
    }

    @Override protected boolean onInsufficientResources(Region region) {
        return true;
    }

    @Override public String getName() {
        return "regenerate-power";
    }

    @Override public void onInit(ITownshipsPlugin plugin) {
    }

    @Override public void onLoad(ITownshipsPlugin plugin, Region region, StoredDataSection data) {
        super.onLoad(plugin, region, data);
        drainAmount = Integer.valueOf(data.get("amount", "0"));
    }

    @Override public void onUnload(ITownshipsPlugin plugin, Region region, StoredDataSection data) {
        super.onUnload(plugin, region, data);
        data.set("amount", drainAmount);
    }

}
