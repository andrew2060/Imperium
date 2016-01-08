package net.kingdomsofarden.townships.effects;

import net.kingdomsofarden.townships.api.effects.TickableEffect;
import net.kingdomsofarden.townships.api.regions.FunctionalRegion;

public class EffectTask {

    private FunctionalRegion region;
    private TickableEffect effect;
    private double load;
    private long nextTick;
    private long scheduledTime;
    private boolean reschedule;
    private int sampleTolerance;
    private double[] enqueuedAverages;
    private int queueHead;

    public EffectTask(FunctionalRegion region, TickableEffect effect) {
        this.region = region;
        this.effect = effect;
        this.nextTick = effect.startTime();
        this.reschedule = true;
        this.sampleTolerance = 10; // TODO something a bit more personalized for effects perhaps?
        this.enqueuedAverages = new double[sampleTolerance];
        this.queueHead = 0;
    }

    public void tick() {
        if (region.isValid()) {
            long startTime = System.currentTimeMillis();
            nextTick = effect.onTick(this.region, scheduledTime);
            long endTime = System.currentTimeMillis();
            // Profile for effect balancing
            double duration = endTime - startTime;
            double loadval = duration/sampleTolerance;
            load -= enqueuedAverages[queueHead];
            enqueuedAverages[queueHead] = loadval;
            load += loadval;
            queueHead++;
            if (queueHead >= sampleTolerance) {
                queueHead = 0;
            }
            // Reschedule
            if (nextTick == Long.MAX_VALUE) {
                reschedule = false;
            }
        } else {
            nextTick = Long.MAX_VALUE;
            reschedule = false;
        }
    }

    public double getLoad() {
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
