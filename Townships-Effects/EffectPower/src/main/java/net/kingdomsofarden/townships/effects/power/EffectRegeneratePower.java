package net.kingdomsofarden.townships.effects.power;

import net.kingdomsofarden.townships.api.ITownshipsPlugin;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.util.StoredDataSection;
import net.kingdomsofarden.townships.effects.common.EffectPeriodicCost;
import net.kingdomsofarden.townships.util.MetaKeys;

import java.util.Map;

public class EffectRegeneratePower extends EffectPeriodicCost {

    private int regenAmount;
    private int max;

    @Override
    protected boolean onSuccessfulTick(Region region) {
        Map<String, Object> meta = region.getMetadata();
        if (!meta.containsKey(MetaKeys.POWER)) { // Doesn't have power meta, don't reschedule
            return false;
        }
        int curr = (Integer) meta.get(MetaKeys.POWER);
        curr += regenAmount; // TODO call event
        if (max != -1 && curr > max) {
            curr = max;
        }
        meta.put(MetaKeys.POWER, curr);
        return true;
    }

    @Override
    protected boolean onInsufficientResources(Region region) {
        return true;
    }

    @Override
    public String getName() {
        return "regenerate-power";
    }

    @Override
    public void onInit(ITownshipsPlugin plugin) {}


    @Override
    public void onLoad(ITownshipsPlugin plugin, Region region, StoredDataSection data) {
        super.onLoad(plugin, region, data);
        regenAmount = Integer.valueOf(data.get("amount", "0"));
        max = Integer.valueOf(data.get("max-amount", "-1"));
    }

    @Override
    public void onUnload(ITownshipsPlugin plugin, Region region, StoredDataSection data) {
        super.onUnload(plugin, region, data);
        data.set("amount", regenAmount);
        data.set("max-amount", max);

    }



}
