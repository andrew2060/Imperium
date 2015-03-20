package net.kingdomsofarden.townships.regions.collections;

import net.kingdomsofarden.townships.regions.TownshipsRegion;

import java.util.LinkedList;

public class RegionTaskStack extends LinkedList<TownshipsRegion> {

    private double loadFactor;

    @Override
    public boolean add(TownshipsRegion region) {
        if (super.add(region)) {
            loadFactor += region.getLoad();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void add(int index, TownshipsRegion region) {
        super.add(index, region);
        loadFactor += region.getLoad();
    }

    @Override
    public boolean remove(Object region) {
        if (!(region instanceof TownshipsRegion)) {
            return false;
        }
        if (super.remove(region)) {
            loadFactor -= ((TownshipsRegion) region).getLoad();
            return true;
        } else {
            return false;
        }
    }

    public double getLoadFactor() {
        return loadFactor;
    }

    public void tick(boolean runProfiling) {
        if (runProfiling) {
            loadFactor = 0;
        }
        for (TownshipsRegion r : this) {
            r.tick(runProfiling);
            if (runProfiling) {
                loadFactor += r.getLoad();
            }
        }
        return;
    }
}
