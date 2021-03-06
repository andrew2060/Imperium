package net.kingdomsofarden.townships.api.permissions;

import java.util.Collections;
import java.util.HashSet;

/**
 * Types of access available to a given region
 */
public final class AccessType {

    /**
     * Block Access: Interact with Containers
     */
    public static final AccessType ACCESS = new AccessType("ACCESS");
    /**
     * Block Access: Interact with Redstone Components - will automatically have the "ACCESS" access level
     */
    public static final AccessType INTERACT = new AccessType("INTERACT", AccessType.ACCESS);
    /**
     * Administrative Access: Manage regional finances, i.e. paying for upkeep
     */
    public static final AccessType TREASURER = new AccessType("TREASURER");
    /**
     * Administrative Access: Grants region creation rights for lower tiered regions, as well as treasurer rights
     */
    public static final AccessType ZONING = new AccessType("ZONING", AccessType.TREASURER);
    /**
     * Administrative Access: Manage regional diplomacy with other regions
     */
    public static final AccessType DIPLOMAT = new AccessType("DIPLOMAT");
    /**
     * Administrative Access: Grants all administrative roles by default as well as grants region destruction rights
     */
    public static final AccessType GOVERNOR =
        new AccessType("GOVERNOR", AccessType.ZONING, AccessType.TREASURER, AccessType.DIPLOMAT);
    /**
     * Administrative Access: Manage citizen acceptance/rejection/removal
     */
    public static final AccessType IMMIGRATION = new AccessType("IMMIGRATION");
    /**
     * Block Access: Build/Break Blocks - will automatically have all other block access levels
     */
    public static AccessType CONSTRUCT =
        new AccessType("CONSTRUCT", AccessType.INTERACT, AccessType.ACCESS);
    private final String name;
    private HashSet<AccessType> childColl;

    AccessType(String name, AccessType... children) {
        this.name = name;
        this.childColl = new HashSet<AccessType>();
        Collections.addAll(this.childColl, children);
    }

    public static AccessType valueOf(String name) {
        return new AccessType(name.toUpperCase());
    }

    public boolean hasAccess(AccessType type) {
        return this == type || childColl.contains(type);
    }

    @Override public int hashCode() {
        return this.name.hashCode();
    }

    @Override public boolean equals(Object other) {
        return other instanceof AccessType && ((AccessType) other).name.equals(this.name);
    }

}
