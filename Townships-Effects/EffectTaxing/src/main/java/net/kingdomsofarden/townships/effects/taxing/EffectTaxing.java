package net.kingdomsofarden.townships.effects.taxing;

import net.kingdomsofarden.townships.api.ITownshipsPlugin;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.util.StoredDataSection;
import net.kingdomsofarden.townships.effects.common.EffectPeriodic;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class EffectTaxing extends EffectPeriodic {

    private Map<String, Collection<TaxItem>> taxes;

    private Map<String, Collection<TaxItem>> outstanding;

    @Override
    public String getName() {
        return "taxing";
    }

    @Override
    public void onInit(ITownshipsPlugin plugin) {

    }

    @Override
    public long onTick(Region region, long time) {
        for (Region child : region.getChildren()) {
            if (taxes.containsKey(child.getType().toLowerCase())) {
                for (TaxItem tax : taxes.get(child.getType().toLowerCase())) {
                    if (!tax.tax(region, child)) {
                        String key = child.getName().or(region.getUid().toString());
                        if (outstanding.containsKey(key) && outstanding.get(key) != null) {
                            outstanding.get(key).add(tax);
                        } else {
                            outstanding.put(key, new HashSet<TaxItem>());
                            outstanding.get(key).add(tax);
                        }
                    }
                }
            }
        }
        return super.onTick(region, time);
    }

    @Override
    public void onLoad(ITownshipsPlugin plugin, Region region, StoredDataSection data) {
        taxes = new HashMap<String, Collection<TaxItem>>();
        outstanding = new HashMap<String, Collection<TaxItem>>();

    }

    @Override
    public void onUnload(ITownshipsPlugin plugin, Region region, StoredDataSection data) {

    }
}
