package net.kingdomsofarden.townships.api.events;

import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.relations.RelationState;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RegionRelationChangeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private Region origin;
    private Region target;
    private RelationState current;
    private RelationState relation;

    public RegionRelationChangeEvent(Region declarer, Region declaree, RelationState current, RelationState relation) {
        this.origin = declarer;
        this.target = declaree;
        this.current = current;
        this.relation = relation;
        this.cancelled = false;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Region getOrigin() {
        return origin;
    }

    public Region getTarget() {
        return target;
    }

    public RelationState getRelation() {
        return relation;
    }

    public RelationState getCurrentRelation() {
        return current;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
