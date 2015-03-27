package net.kingdomsofarden.townships.regions;

import com.google.common.base.Optional;
import com.google.common.collect.HashMultimap;
import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.effects.Effect;
import net.kingdomsofarden.townships.api.effects.TickableEffect;
import net.kingdomsofarden.townships.api.permissions.RoleGroup;
import net.kingdomsofarden.townships.api.regions.Area;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.util.BoundingBox;
import net.kingdomsofarden.townships.api.util.StoredDataSection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
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
    private Collection<TickableEffect> tickableEffects;

    private BoundingBox bounds;
    private Location center;

    private Collection<Area> containingAreas;

    public TownshipsRegion(StoredDataSection config) {
        containingAreas = new LinkedList<Area>();
        rolesByCitizenUid = HashMultimap.create();
        citizenUidsByRole = HashMultimap.create();
        StoredDataSection groupAssignments = config.getSection("roles");
        for (String roleName : groupAssignments.getKeys(false)) {
            RoleGroup group = RoleGroup.valueOf(roleName);
            for (String uid : groupAssignments.<String>getList(roleName)) {
                try {
                    UUID id = UUID.fromString(uid);
                    rolesByCitizenUid.put(id, group);
                    citizenUidsByRole.put(group, id);
                } catch (IllegalArgumentException e) {
                    // TODO debug message
                }
            }
        }
        //TODO
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
    public Location getLocation() {
        return center;
    }

    @Override
    public BoundingBox getBounds() {
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

    public Collection<Area> getBoundingAreas() {
        return containingAreas;
    }
}
