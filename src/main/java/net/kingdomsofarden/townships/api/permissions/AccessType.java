package net.kingdomsofarden.townships.api.permissions;

/**
 * Types of access available to a given region
 */
public enum AccessType {
    /**
     * Build/Break Blocks - will automatically have all other access levels
     */
    CONSTRUCT,
    /**
     * Interact with Redstone Components - will automatically have the "ACCESS" access level
     */
    INTERACT,
    /**
     * Interact with Containers
     */
    ACCESS;

    public boolean isHigherThanOrSame(AccessType other) {
        switch (this) {
            case CONSTRUCT:
                return true;
            case INTERACT:
                return other != AccessType.CONSTRUCT;
            case ACCESS:
                return other == AccessType.ACCESS;
        }
        return false;
    }
}
