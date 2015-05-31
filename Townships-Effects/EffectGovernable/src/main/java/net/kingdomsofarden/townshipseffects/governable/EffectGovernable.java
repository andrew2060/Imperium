package net.kingdomsofarden.townshipseffects.governable;

import net.kingdomsofarden.townships.api.ITownshipsPlugin;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.effects.common.EffectPeriodic;

public class EffectGovernable extends EffectPeriodic {
    @Override
    public String getName() {
        return "governable";
    }

    class Stats {
        private int pop;
        private int land;
        private double prod;
        private double iti;

        private double itr;
        private double gnp;
        private double exp;
        private double nni;
    }

    Stats curr;
    Stats last;


    @Override
    public void onInit(ITownshipsPlugin plugin) {

    }

    @Override
    public long onTick(Region region, long time) {
        return super.onTick(region, time);
    }
}
