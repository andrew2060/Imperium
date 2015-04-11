package net.kingdomsofarden.townships.effects.core;

import net.kingdomsofarden.townships.api.ITownshipsPlugin;
import net.kingdomsofarden.townships.api.effects.Effect;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.util.StoredDataSection;
import net.kingdomsofarden.townships.listeners.ExplosiveProtectionListener;
import org.bukkit.event.Listener;

public class EffectDenyExplosions implements Effect, Listener {

    private Region region;

    @Override
    public String getName() {
        return "deny-explosions";
    }

    @Override
    public void onInit(ITownshipsPlugin plugin) {

    }

    @Override
    public void onLoad(ITownshipsPlugin plugin, Region region, StoredDataSection data) {
        this.region = region;
        ExplosiveProtectionListener.instance.registerProtectionEffect(this);
    }

    @Override
    public void onUnload(ITownshipsPlugin plugin, Region region, StoredDataSection data) {
        ExplosiveProtectionListener.instance.unregisterProtectionEffect(this);
        this.region = null;
    }

    @Override
    public Region getRegion() {
        return region;
    }

    @Override
    public int hashCode() {
        return region.hashCode()+ getName().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof EffectDenyExplosions) {
            return region.equals(((EffectDenyExplosions) other).getRegion());
        } else {
            return false;
        }
    }



}
