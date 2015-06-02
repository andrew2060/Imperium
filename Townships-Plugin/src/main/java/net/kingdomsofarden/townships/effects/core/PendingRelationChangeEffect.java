package net.kingdomsofarden.townships.effects.core;

import net.kingdomsofarden.townships.api.ITownshipsPlugin;
import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.api.effects.TickableEffect;
import net.kingdomsofarden.townships.api.events.RegionRelationChangeEvent;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.relations.RelationState;
import net.kingdomsofarden.townships.api.util.StoredDataSection;
import net.kingdomsofarden.townships.util.I18N;
import net.kingdomsofarden.townships.util.Messaging;
import org.bukkit.Bukkit;

import java.util.UUID;

public class PendingRelationChangeEffect implements TickableEffect {

    private long time;
    private RelationState change;
    private Region target;

    @Override public long startTime() {
        return time < System.currentTimeMillis() ? -1 : time;
    }

    @Override public long onTick(Region region, long time) {
        RegionRelationChangeEvent event =
            new RegionRelationChangeEvent(region, target, region.getRelations().get(target),
                change);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            switch (event.getRelation()) {
                case WAR:
                    Messaging.broadcastFormattedMessage(I18N.WAR_STARTED, region, target);
                    break;
                case PEACE:
                    RelationState base = event.getCurrentRelation();
                    if (base.equals(RelationState.ALLIANCE_PENDING_PEACE)) {
                        Messaging.broadcastFormattedMessage(I18N.ALLIANCE_CANCELED, region, target);
                    } else {
                        if (target.getRelations().getOrDefault(region, RelationState.PEACE)
                            .getBaseType().equals(RelationState.PEACE)) {
                            Messaging.broadcastFormattedMessage(I18N.WAR_ENDED, region, target);
                        }
                    }
                    break;
                case ALLIANCE:
                    Messaging.broadcastFormattedMessage(I18N.ALLIANCE_STARTED, region, target);
                    break;
            }
            region.getRelations().put(target, change);
        } else { // May cause weird behaviour/should not be called, just here for safety
            RelationState reset = RelationState.PEACE;
            switch (event.getCurrentRelation()) {
                case WAR_PENDING_PEACE:
                    reset = RelationState.WAR;
                    break;
                case ALLIANCE_PENDING_PEACE:
                    reset = RelationState.ALLIANCE;
                    break;
            }
            region.getRelations().put(target, reset);
            target.getExternRelations().put(region, reset);
        }
        return Long.MAX_VALUE;
    }

    @Override public String getName() {
        return "pending-relation-change";
    }

    @Override public void onInit(ITownshipsPlugin plugin) {
    }

    @Override public void onLoad(ITownshipsPlugin plugin, Region r, StoredDataSection data) {
        time = Long.valueOf(data.get("start-time", "0"));
        change = RelationState.valueOf(data.get("relation", "PEACE"));
        target = Townships.getRegions().get(UUID.fromString(data.get("region", null))).orNull();
    }

    @Override public void onUnload(ITownshipsPlugin plugin, Region region, StoredDataSection data) {
        data.set("start-time", time);
        data.set("relation", change.name());
        data.set("region", target.getUid());
    }

    @Override public Region getRegion() {
        return target;
    }
}
