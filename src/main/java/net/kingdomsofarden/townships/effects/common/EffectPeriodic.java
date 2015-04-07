package net.kingdomsofarden.townships.effects.common;

import net.kingdomsofarden.townships.api.effects.TickableEffect;
import net.kingdomsofarden.townships.api.regions.Region;

public abstract class EffectPeriodic implements TickableEffect {

    protected long lastTick;
    protected long period;
    protected Region region;

    @Override
    public long lastTick() {
        return lastTick;
    }

    @Override
    public long period() {
        return period;
    }

    @Override
    public long onTick(Region region, long time) {
        lastTick = time;
        return time + period;
    }

    @Override
    public Region getRegion() {
        return region;
    }
}
