package net.kingdomsofarden.townships.api.permissions;

import java.util.Collections;
import java.util.HashSet;

/**
 * Types of access available to a given region
 */
public final class AccessType {

    /**
     * Block Access: Build/Break Blocks - will automatically have all other block access levels
     */
    public static AccessType CONSTRUCT = new AccessType("CONSTRUCT", AccessType.INTERACT, AccessType.ACCESS);
    /**
     * Block Access: Interact with Redstone Components - will automatically have the "ACCESS" access level
     */
    public static final AccessType INTERACT= new AccessType("INTERACT", AccessType.ACCESS);
    /**
     * Block Access: Interact with Containers
     */
    public static final AccessType ACCESS = new AccessType("ACCESS");
    /**
     * Administrative Access: Grants all administrative roles by default as well as grants region destruction rights
     */
    public static final AccessType GOVERNOR = new AccessType("GOVERNOR", AccessType.ZONING, AccessType.TREASURER);
    /**
     * Administrative Access: Grants region creation rights for lower tiered regions, as well as treasurer rights
     */
    public static final AccessType ZONING = new AccessType("ZONING", AccessType.TREASURER);
    /**
     * Administrative Access: Manage regional finances, i.e. paying for upkeep
     */
    public static final AccessType TREASURER = new AccessType("TREASURER");

    public static final AccessType valueOf(String name) {
        return new AccessType(name.toUpperCase());
    }

    private HashSet<AccessType> childColl;

    AccessType(String name, AccessType... children) {
        this.childColl = new HashSet<AccessType>();
        Collections.addAll(this.childColl, children);
    }

    public boolean hasAccess(AccessType type) {
        return this == type || childColl.contains(type);
    }



}
