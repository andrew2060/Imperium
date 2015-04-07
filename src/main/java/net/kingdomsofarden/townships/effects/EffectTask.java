package net.kingdomsofarden.townships.effects;

import net.kingdomsofarden.townships.api.effects.TickableEffect;
import net.kingdomsofarden.townships.api.regions.Region;

public class EffectTask {
    private Region region;
    private TickableEffect effect;
    private long load;
    private long nextTick;
    private long scheduledTime;
    private boolean reschedule;

    public EffectTask(Region region, TickableEffect effect) {
        this.region = region;
        this.effect = effect;
        this.nextTick = effect.lastTick();
        this.reschedule = true;
    }

    public void tick() {
        if (region.isValid()) {
            long startTime = System.currentTimeMillis();
            nextTick = effect.onTick(this.region, scheduledTime);
            long endTime = System.currentTimeMillis();
            load = (load + (endTime - startTime)) / 2;
            if (nextTick == Long.MAX_VALUE) {
                reschedule = false;
            }
        } else {
            nextTick = Long.MAX_VALUE;
            reschedule = false;
        }
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

    public boolean isReschedulable() {
        return reschedule;
    }
}