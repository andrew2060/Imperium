package net.kingdomsofarden.townships.effects;

import net.kingdomsofarden.townships.api.effects.TickableEffect;
import net.kingdomsofarden.townships.api.regions.Region;

public class EffectTask {
    private Region region;
    private TickableEffect effect;
    private long load;
    private long nextTick;
    private long scheduledTime;

    public EffectTask(Region region, TickableEffect effect) {
        this.region = region;
        this.effect = effect;
        this.nextTick = effect.startTime();
    }

    public void tick() {
        long startTime = System.currentTimeMillis();
        effect.onTick(this.region);
        long endTime = System.currentTimeMillis();
        load = (load + (endTime - startTime))/2;
        nextTick = scheduledTime + effect.delay();
    }

    public long getLoad() {
        return load;
    }

    public long getNextTick() {
        return nextTick;
    }

    public void setScheduledTime(long scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public TickableEffect getTickable() {
        return effect;
    }
}