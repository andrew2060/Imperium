package net.kingdomsofarden.townships.effects;

import java.util.LinkedList;

public class EffectTaskStack extends LinkedList<EffectTask> {

    int position;
    double load; // Load - the sum of the individual tick times of the various components

    public EffectTaskStack(int position) {
        load = 0;
        this.position = position;
    }

    @Override public void push(EffectTask task) {
        super.push(task);
        load +=
            task.getLoad() + 1; // Increment always by one to denote presence of 0ms time effects
    }

    @Override public EffectTask pollLast() {
        EffectTask ret = super.pollLast();
        load -= ret.getLoad() + 1;
        return ret;
    }

    public double getLoad() {
        return load;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

}
