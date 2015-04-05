package net.kingdomsofarden.townships.api.permissions;

import java.util.Collections;
import java.util.HashSet;

/**
 * Types of access available to a given region
 */
public enum AccessType implements Comparable<AccessType> {

    /**
     * Block Access: Build/Break Blocks - will automatically have all other block access levels
     */
    CONSTRUCT(AccessType.INTERACT, AccessType.ACCESS),
    /**
     * Block Access: Interact with Redstone Components - will automatically have the "ACCESS" access level
     */
    INTERACT(AccessType.ACCESS),
    /**
     * Block Access: Interact with Containers
     */
    ACCESS,
    /**
     * Administrative Access: Grants all administrative roles by default as well as grants region destruction rights
     */
    GOVERNOR(AccessType.ZONING, AccessType.TREASURER),
    /**
     * Administrative Access: Grants region creation rights for lower tiered regions, as well as treasurer rights
     */
    ZONING(AccessType.TREASURER),
    /**
     * Administrative Access: Manage regional finances, i.e. paying for upkeep
     */
    TREASURER;

    private HashSet<AccessType> childColl;

    AccessType(AccessType... children) {
        this.childColl = new HashSet<AccessType>();
        Collections.addAll(this.childColl, children);
    }

    public boolean hasAccess(AccessType type) {
        return this == type || childColl.contains(type);
    }



}
