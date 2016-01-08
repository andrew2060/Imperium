package net.kingdomsofarden.townships.effects.common;

import net.kingdomsofarden.townships.api.ITownshipsPlugin;
import net.kingdomsofarden.townships.api.effects.TickableEffect;
import net.kingdomsofarden.townships.api.regions.FunctionalRegion;
import net.kingdomsofarden.townships.api.util.Serializer;
import net.kingdomsofarden.townships.api.util.StoredDataSection;

public abstract class EffectPeriodic implements TickableEffect {

    protected long startTime;
    protected long period;
    protected FunctionalRegion region;

    @Override public long startTime() {
        return startTime;
    }

    @Override public long onTick(FunctionalRegion region, long time) {
        startTime = time;
        return time + period;
    }

    @Override public FunctionalRegion getRegion() {
        return region;
    }

    @Override public void onLoad(ITownshipsPlugin plugin, FunctionalRegion r, StoredDataSection data) {
        startTime = Long.valueOf(data.get("start-time", "-1"));
        period = data.get("period", new Serializer<Long>() {
            @Override public String serialize(Long obj) {
                return obj + "";
            }

            @Override public Long deserialize(String input) {
                return Long.valueOf(input);
            }
        }, Long.MAX_VALUE);
        region = r;
    }

    @Override public void onUnload(ITownshipsPlugin plugin, FunctionalRegion region, StoredDataSection data) {
        data.set("period", period);
        data.set("start-time", startTime);
    }
}
