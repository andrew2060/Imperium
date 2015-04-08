package net.kingdomsofarden.townships.regions;

import com.google.common.base.Optional;
import com.google.common.collect.HashMultimap;
import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.effects.Effect;
import net.kingdomsofarden.townships.api.permissions.AccessType;
import net.kingdomsofarden.townships.api.permissions.RoleGroup;
import net.kingdomsofarden.townships.api.regions.Area;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.regions.bounds.RegionBoundingBox;
import net.kingdomsofarden.townships.api.resources.EconomyProvider;
import net.kingdomsofarden.townships.api.resources.ResourceProvider;
import net.kingdomsofarden.townships.api.util.Serializer;
import net.kingdomsofarden.townships.api.util.StoredDataSection;
import net.kingdomsofarden.townships.regions.bounds.RegionAxisAlignedBoundingBox;
import net.kingdomsofarden.townships.util.LocationSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

public class TownshipsRegion implements Region {

    private UUID regionUid;
    private String name;
    private int tier;

    private HashMultimap<UUID, RoleGroup> rolesByCitizenUid;
    private HashMultimap<RoleGroup, UUID> citizenUidsByRole;

    private HashMultimap<UUID, AccessType> accessByCitizenUid;
    private HashMultimap<RoleGroup, AccessType> accessByRole;

    private Map<String, Integer> maxTypeInRegion;
    private Map<Integer, Integer> maxTierInRegion;

    private Map<String, Effect> effects;

    private RegionBoundingBox bounds;

    private Location pos1;
    private Location pos2;

    private Collection<Area> containingAreas;
    private String type;

    private TreeSet<Region> parents;
    private TreeSet<Region> children;

    private boolean valid;

    public TownshipsRegion(UUID rId, StoredDataSection config) {

        // Set up basic data structures
        valid = true;
        containingAreas = new LinkedList<Area>();
        rolesByCitizenUid = HashMultimap.create();
        citizenUidsByRole = HashMultimap.create();
        accessByCitizenUid = HashMultimap.create();
        accessByRole = HashMultimap.create();
        Comparator<Region> regionComparator = new Comparator<Region>() {
            @Override
            public int compare(Region o1, Region o2) {
                int ret = o2.getTier() - o1.getTier();
                if (ret == 0) {
                    return o1.getUid().compareTo(o2.getUid());
                } else {
                    return ret;
                }
            }
        };
        parents = new TreeSet<Region>(regionComparator);
        children = new TreeSet<Region>(regionComparator);
        // Populate region identifier data
        regionUid = rId;
        name = config.get("name", null);
        type = config.get("type", "UNDEFINED");
        Serializer<Integer> intSerializer = new Serializer<Integer>() {
            @Override
            public String serialize(Integer obj) {
                return obj + "";
            }

            @Override
            public Integer deserialize(String input) {
                return (int) Double.valueOf(input).doubleValue();
            }
        };
        tier = config.get("tier", intSerializer, -1);
        pos1 = config.get("position-1", new LocationSerializer(), null);
        pos2 = config.get("position-2", new LocationSerializer(), null);
        if (pos1 == null || pos2 == null) {
            throw new IllegalStateException("Problem loading location of region " + regionUid + ": null");
        }
        if (pos1.getWorld() != pos2.getWorld()) {
            throw new IllegalStateException("Mismatched worlds");
        }
        bounds = new RegionAxisAlignedBoundingBox(this, pos1, pos2);
        StoredDataSection roleSection = config.getSection("roles");
        for (String roleName : roleSection.getKeys(false)) {
            RoleGroup group = RoleGroup.valueOf(roleName);
            for (String uid : roleSection.<String>getList(roleName)) {
                try {
                    UUID id = UUID.fromString(uid);
                    rolesByCitizenUid.put(id, group);
                    citizenUidsByRole.put(group, id);
                } catch (IllegalArgumentException e) {
                    // TODO debug message
                }
            }
        }
        effects = new HashMap<String, Effect>();
        StoredDataSection effectSection = config.getSection("effects");
        for (String eName : effectSection.getKeys(false)) {
            Effect e = Townships.getEffectManager().loadEffect(eName, this, effectSection.getSection(eName));
            effects.put(eName.toLowerCase(), e);
        }
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public UUID getUid() {
        return regionUid;
    }

    @Override
    public Optional<String> getName() {
        return Optional.fromNullable(name);
    }

    @Override
    public Collection<UUID> getRole(RoleGroup group) {
        return citizenUidsByRole.get(group);
    }

    @Override
    public Collection<Citizen> getCitizensInBounds() {
        HashSet<Citizen> contents = new HashSet<Citizen>();
        for (Area a : containingAreas) {
            for (Citizen c : a.getCitizensInArea()) {
                Player p = Bukkit.getPlayer(c.getUid());
                if (p != null && bounds.isInBounds(p.getLocation())) {
                    contents.add(c);
                }
            }
        }
        return contents;
    }

    @Override
    public RegionBoundingBox getBounds() {
        return bounds;
    }

    @Override
    public Collection<Effect> getEffects() {
        return effects.values();
    }

    @Override
    public boolean hasEffect(String name) {
        return effects.containsKey(name.toLowerCase());
    }

    @Override
    public <T extends Effect> T getEffect(String name) throws IllegalStateException {
        T ret = (T) effects.get(name.toLowerCase());
        if (ret == null) {
            throw new IllegalStateException("An attempt to retrieve the effect " + name + " was made when it did not exist on a region");
        }
        return ret;
    }

    @Override
    public Collection<RoleGroup> getRoles(Citizen citizen) {
        return rolesByCitizenUid.get(citizen.getUid());
    }

    @Override
    public void saveConfigs(StoredDataSection data) {
        //TODO
    }

    @Override
    public String getType() {
        return type;
    }

    public Collection<Area> getBoundingAreas() {
        return containingAreas;
    }

    @Override
    public void addRole(Citizen citizen, RoleGroup group) {
        rolesByCitizenUid.put(citizen.getUid(), group);
        citizenUidsByRole.put(group, citizen.getUid());
    }

    @Override
    public boolean removeRole(Citizen citizen, RoleGroup group) {
        boolean ret = rolesByCitizenUid.remove(citizen.getUid(), group);
        boolean ret2 = citizenUidsByRole.remove(group, citizen.getUid());
        return ret || ret2;
    }

    @Override
    public boolean hasAccess(Citizen citizen, AccessType type, Set<RoleGroup> effectiveGroups) { // TODO further investigate if not n^2 algorithm is possible
        if (citizen.isRoot()) {
            return true;
        } else {
            for (AccessType access :accessByCitizenUid.get(citizen.getUid())) {
                if (access.hasAccess(type)) {
                    return true;
                }
            }
            for (RoleGroup group : effectiveGroups) {
                for (AccessType access :accessByRole.get(group)) {
                    if (access.hasAccess(type)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public Collection<EconomyProvider> getEconomyProviders() {
        return null; //TODO
    }

    @Override
    public Collection<ResourceProvider> getResourceProviders() {
        return null;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public Collection<Region> getParents() {
        return parents;
    }

    @Override
    public Collection<Region> getChildren() {
        return children;
    }

    @Override
    public boolean isCompatible(Region child) {
        String type = child.getType().toLowerCase();
        int tier = child.getTier();
        boolean careType = maxTypeInRegion.containsKey(type);
        int amtType = maxTypeInRegion.get(type);
        boolean careTier = maxTierInRegion.containsKey(tier);
        int amtTier = maxTierInRegion.get(tier);
        for (Region r : getChildren()) {
            if (careTier && r.getTier() == tier) {
                amtTier--;
            }
            if (careType && r.getType().equalsIgnoreCase(type)) {
                amtType--;
            }
        }
        return !(amtTier <= 0 || amtType <= 0);
    }
}
