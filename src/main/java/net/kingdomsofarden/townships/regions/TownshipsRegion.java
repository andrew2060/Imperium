package net.kingdomsofarden.townships.regions;

import com.google.common.base.Optional;
import com.google.common.collect.HashMultimap;
import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.effects.Effect;
import net.kingdomsofarden.townships.api.permissions.RoleGroup;
import net.kingdomsofarden.townships.api.regions.Area;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.regions.bounds.RegionBoundingBox;
import net.kingdomsofarden.townships.api.util.Serializer;
import net.kingdomsofarden.townships.api.util.StoredDataSection;
import net.kingdomsofarden.townships.regions.bounds.RegionAxisAlignedBoundingBox;
import net.kingdomsofarden.townships.util.LocationSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

public class TownshipsRegion implements Region {

    private UUID regionUid;
    private String name;
    private int tier;

    private HashMultimap<UUID, RoleGroup> rolesByCitizenUid;
    private HashMultimap<RoleGroup, UUID> citizenUidsByRole;

    private Map<String, Effect> effects;

    private RegionBoundingBox bounds;

    private Location pos1;
    private Location pos2;

    private Collection<Area> containingAreas;
    private String type;

    public TownshipsRegion(UUID rId, StoredDataSection config) {
        regionUid = rId;
        containingAreas = new LinkedList<Area>();
        rolesByCitizenUid = HashMultimap.create();
        citizenUidsByRole = HashMultimap.create();
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
}
