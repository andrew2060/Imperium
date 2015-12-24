package net.kingdomsofarden.townships.listeners;

import net.kingdomsofarden.townships.api.ITownshipsPlugin;
import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.events.CitizenEnterRegionEvent;
import net.kingdomsofarden.townships.api.events.CitizenExitRegionEvent;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.relations.RelationState;
import net.kingdomsofarden.townships.api.util.StoredDataSection;
import net.kingdomsofarden.townships.effects.common.EffectPeriodic;
import net.kingdomsofarden.townships.util.Constants;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.UUID;

public class WarListener implements Listener {
    // TODO 2.0 Dynamically bound regions
    @EventHandler(priority = EventPriority.LOWEST) public void onWarDeath(PlayerDeathEvent event) {
        Citizen dead = Townships.getCitizens().getCitizen(event.getEntity().getUniqueId());
        Region defendingRegion = dead.getCitizenRegion();
        if (event.getEntity().getKiller() != null) {
            defendingRegion.updatePower(-1 * Constants.POWER_LOSS_PER_DEATH_PVP);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEnterRegion(CitizenEnterRegionEvent event) {
        Region r = event.getRegion();
        Citizen c = event.getCitizen();
        RelationState rel = r.getEffectiveRelation(c);
        if (rel.equals(RelationState.ALLIANCE) || rel.equals(RelationState.SELF)) {
            r.addEffect(new OccupationEffect(c.getUid(), r, true), false);
        } else if (rel.equals(RelationState.WAR)) {
            r.addEffect(new OccupationEffect(c.getUid(), r, false), false);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onExitRegion(CitizenExitRegionEvent event) {
        Region r = event.getRegion();
        Citizen c = event.getCitizen();
        if (r.hasEffect(c.getUid() + "_occupation")) {
            r.removeEffect(c.getUid() + "_occupation");
        }
    }

    private static class OccupationEffect extends EffectPeriodic {

        private final Region region;
        private final boolean flag;
        private String name;

        public OccupationEffect(UUID uid, Region region, boolean friendly) {
            this.name = uid + "_occupation";
            this.region = region;
            this.flag = friendly;
            this.startTime = System.currentTimeMillis() + Constants.PERIOD_OCCUPATION_UPDATE;
            this.period = Constants.PERIOD_OCCUPATION_UPDATE;
        }

        @Override public String getName() {
            return name;
        }

        @Override public long onTick(Region r, long time) {
            r.updatePower(flag ?
                Constants.POWER_GAIN_PER_TICK_OCCUPATION :
                Constants.POWER_LOSS_PER_TICK_OCCUPATION * -1);
            return super.onTick(r, time);
        }

        @Override public void onInit(ITownshipsPlugin plugin) {
        }

        @Override
        public void onLoad(ITownshipsPlugin plugin, Region region, StoredDataSection data) {
            // Don't store any information
        }

        @Override
        public void onUnload(ITownshipsPlugin plugin, Region region, StoredDataSection data) {
            // Don't store any information
        }

        @Override public Region getRegion() {
            return region;
        }
    }
}
