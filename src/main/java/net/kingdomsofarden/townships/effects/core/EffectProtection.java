package net.kingdomsofarden.townships.effects.core;

import net.kingdomsofarden.townships.api.ITownshipsPlugin;
import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.effects.Effect;
import net.kingdomsofarden.townships.api.permissions.AccessType;
import net.kingdomsofarden.townships.api.permissions.RoleGroup;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.util.StoredDataSection;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class EffectProtection implements Effect {

    private Region region;
    private HashMap<UUID, AccessType> userAccess;
    private HashMap<RoleGroup, AccessType> groups;


    @Override
    public String getName() {
        return "protection";
    }

    @Override
    public void onLoad(ITownshipsPlugin plugin, Region region, StoredDataSection data) {
        this.userAccess = new HashMap<UUID, AccessType>();
        this.groups = new HashMap<RoleGroup, AccessType>();
        this.region = region;
        StoredDataSection construct = data.getSection("construct");
        for (String groupName : construct.<String>getList("roles")) {
            groups.put(RoleGroup.valueOf(groupName), AccessType.CONSTRUCT);
        }
        for (String uid : construct.<String>getList("citizens")) {
            try {
                UUID id = UUID.fromString(uid);
                userAccess.put(id, AccessType.CONSTRUCT);
            } catch (IllegalArgumentException e) {
                // TODO debug message
            }
        }
        StoredDataSection interact = data.getSection("interact");
        for (String groupName : interact.<String>getList("roles")) {
            RoleGroup r = RoleGroup.valueOf(groupName);
            if (!groups.containsKey(r) || (groups.containsKey(r) && AccessType.INTERACT.isHigherThanOrSame(groups.get(r)))) {
                groups.put(r, AccessType.INTERACT);
            }
        }
        for (String uid : interact.<String>getList("citizens")) {
            try {
                UUID id = UUID.fromString(uid);
                if (!userAccess.containsKey(id) || (userAccess.containsKey(id) && AccessType.INTERACT.isHigherThanOrSame(userAccess.get(id)))) {
                    userAccess.put(id, AccessType.INTERACT);
                }
            } catch (IllegalArgumentException e) {
                // TODO debug message
            }
        }
        StoredDataSection access = data.getSection("access");
        for (String groupName : access.<String>getList("roles")) {
            RoleGroup r = RoleGroup.valueOf(groupName);
            if (!(groups.containsKey(r))) {
                groups.put(r, AccessType.ACCESS);
            }
        }
        for (String uid : access.<String>getList("citizens")) {
            try {
                UUID id = UUID.fromString(uid);
                if (!(userAccess.containsKey(id))) {
                    userAccess.put(id, AccessType.ACCESS);
                }
            } catch (IllegalArgumentException e) {
                // TODO debug message
            }
        }
    }




    @Override
    public void onUnload(ITownshipsPlugin plugin, Region region, StoredDataSection data) {
        this.region = null;
        // Do nothing, this is a core effect and listeners will handle logic
    }

    @Override
    public Region getRegion() {
        return region;
    }

    public boolean isPermitted(AccessType type, Citizen citizen, Set<RoleGroup> effective) {
        UUID uid = citizen.getUid();
        // Check if group access is sufficient
        for (RoleGroup group : effective) {
            if (groups.containsKey(group) && groups.get(group).isHigherThanOrSame(type)) {
                return true;
            }
        }
        // Check individual user access
        return userAccess.containsKey(uid) && userAccess.get(uid).isHigherThanOrSame(type);
    }

}
