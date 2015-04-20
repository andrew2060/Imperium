package net.kingdomsofarden.townships.api.events;

import net.kingdomsofarden.townships.api.regions.Region;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Is called when a region is about to disband. <br>
 * <b>Important:</b> Does not respect cancellation state when region is disbanding due to failure to meet requirements
 */
public class RegionDisbandEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final DisbandCause cause;
    private boolean cancelled;
    private Region region;

    public static class DisbandCause {

        public static final DisbandCause COMMAND = new DisbandCause("COMMAND");
        public static final DisbandCause UPKEEP = new DisbandCause("UPKEEP");
        public static final DisbandCause BLOCK_REQUIREMENTS_NOT_MET = new DisbandCause("BLOCK_REQUIREMENTS_NOT_MET");
        public static final DisbandCause SUBREGION_REQUIREMENTS_NOT_MET = new DisbandCause("SUBREGION_REQUIREMENTS_NOT_MET");

        public static DisbandCause valueOf(String name) {
            return new DisbandCause(name.toUpperCase());
        }


        private final String name;

        public DisbandCause(String name) {
            this.name = name;
        }

        @Override
        public int hashCode() {
            return this.name.hashCode();
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof DisbandCause && ((DisbandCause) other).name.equals(this.name);
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public RegionDisbandEvent(Region region, DisbandCause cause) {
        this.region = region;
        this.cancelled = false;
        this.cause = cause;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * @return The region being disbanded
     */
    public Region getRegion() {
        return region;
    }

    /**
     * @return The cause for disbandment
     */
    public DisbandCause getCause() {
        return cause;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
