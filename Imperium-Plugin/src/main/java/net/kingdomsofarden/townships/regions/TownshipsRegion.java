package net.kingdomsofarden.townships.regions;

import com.google.common.base.Optional;
import com.google.common.collect.HashMultimap;
import net.kingdomsofarden.townships.TownshipsPlugin;
import net.kingdomsofarden.townships.api.Townships;
import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.effects.Effect;
import net.kingdomsofarden.townships.api.permissions.AccessType;
import net.kingdomsofarden.townships.api.permissions.RoleGroup;
import net.kingdomsofarden.townships.api.regions.Area;
import net.kingdomsofarden.townships.api.regions.FunctionalRegion;
import net.kingdomsofarden.townships.api.regions.bounds.RegionBoundingArea;
import net.kingdomsofarden.townships.api.relations.RelationState;
import net.kingdomsofarden.townships.api.resources.EconomyProvider;
import net.kingdomsofarden.townships.api.resources.ItemProvider;
import net.kingdomsofarden.townships.api.util.Serializer;
import net.kingdomsofarden.townships.api.util.StoredDataSection;
import net.kingdomsofarden.townships.tasks.RegionBlockCheckTask;
import net.kingdomsofarden.townships.tasks.RegionSubregionCheckTask;
import net.kingdomsofarden.townships.util.MetaKeys;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class TownshipsRegion implements FunctionalRegion {


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

    private Map<UUID, Double> standings;

    private RegionBoundingArea bounds;

    private Collection<Area> containingAreas;
    private String type;

    private TreeSet<FunctionalRegion> parents;
    private TreeSet<FunctionalRegion> children;

    private Map<String, EconomyProvider> economyProviders;
    private Map<String, ItemProvider> itemProviders;

    private boolean valid;
    private Map<String, Object> metadata;
    private Map<UUID, Map<String, Object>> regionMetadata;
    private Map<FunctionalRegion, RelationState> relations;
    private Map<FunctionalRegion, RelationState> externRelations;
    private Set<UUID> citizens;

    private double maxPower;
    private double currPower;

    public TownshipsRegion(UUID rId, StoredDataSection config) {
        // Set up basic data structures
        valid = true;
        containingAreas = new LinkedList<>();
        rolesByCitizenUid = HashMultimap.create();
        citizenUidsByRole = HashMultimap.create();
        accessByCitizenUid = HashMultimap.create();
        accessByRole = HashMultimap.create();
        maxTypeInRegion = new HashMap<>();
        maxTierInRegion = new HashMap<>();
        metadata = new HashMap<>();
        regionMetadata = new HashMap<>();
        economyProviders = new HashMap<>();
        itemProviders = new HashMap<>();
        Comparator<FunctionalRegion> regionComparator = (o1, o2) -> {
            int ret = o2.getTier() - o1.getTier();
            if (ret == 0) {
                return o1.getUid().compareTo(o2.getUid());
            } else {
                return ret;
            }
        };
        parents = new TreeSet<>(regionComparator);
        children = new TreeSet<>(regionComparator);
        // Populate region identifier data
        regionUid = rId;
        name = config.get("name", null);
        type = config.get("type", "UNDEFINED");
        Serializer<Integer> intSerializer = new Serializer<Integer>() {
            @Override public String serialize(Integer obj) {
                return obj + "";
            }

            @Override public Integer deserialize(String input) {
                return (int) Double.valueOf(input).doubleValue();
            }
        };
        tier = config.get("tier", intSerializer, -1);

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
        effects = new HashMap<>();
        StoredDataSection meta = config.getSection("metadata");
        for (String key : meta.getKeys(false)) {
            metadata.put(key, meta.<ConfigurationSection>getBackingImplementation().get(key));
        }
        StoredDataSection regionalMeta = config.getSection("regional-metadata");
        for (String key : regionalMeta.getKeys(false)) {
            UUID regionUID = UUID.fromString(key);
            StoredDataSection regionMeta = regionalMeta.getSection(key);
            Map<String, Object> regionMetaEntry = new HashMap<>();
            for (String mKey : regionMeta.getKeys(false)) {
                regionMetaEntry.put(mKey,
                    regionalMeta.<ConfigurationSection>getBackingImplementation().get(mKey));
            }
            regionMetadata.put(regionUID, regionMetaEntry);
        }
        StoredDataSection requirements = config.getSection("requirements");
        StoredDataSection maxRegionReqSection = requirements.getSection("region-types-max");
        for (String type : maxRegionReqSection.getKeys(false)) {
            int amt = maxRegionReqSection.get(type, intSerializer, 0);
            if (amt > 0) {
                maxTypeInRegion.put(type.toLowerCase(), amt);
            }
        }
        StoredDataSection maxTierReqSection = requirements.getSection("region-tiers-max");
        for (String tierNum : maxTierReqSection.getKeys(false)) {
            int tier = Integer.parseInt(tierNum);
            int arg = maxTierReqSection.get(tierNum, intSerializer, 0);
            maxTierInRegion.put(tier, arg);
        }
        relations = new HashMap<>();
        externRelations = new HashMap<>();
        StoredDataSection diplomacy = config.getSection("diplomacy");
        StoredDataSection selfDiplomacy = diplomacy.getSection("self");
        StoredDataSection externDiplomacy = diplomacy.getSection("others");
        for (String regionUid : selfDiplomacy.getKeys(false)) {
            UUID uid = UUID.fromString(regionUid);
            RelationState state = RelationState.valueOf(diplomacy.get(regionUid, "PEACE"));
            FunctionalRegion r = Townships.getRegions().get(uid).orNull();
            if (r == null) {
                continue; // TODO log
            }
            relations.put(r, state);
        }
        for (String regionUid : externDiplomacy.getKeys(false)) {
            UUID uid = UUID.fromString(regionUid);
            RelationState state = RelationState.valueOf(diplomacy.get(regionUid, "PEACE"));
            FunctionalRegion r = Townships.getRegions().get(uid).orNull();
            if (r == null) {
                continue; // TODO log
            }
            externRelations.put(r, state);
        }
        citizens = new HashSet<>();
        config.getList("citizens").stream().forEach(s -> citizens.add(UUID.fromString(s)));
        for (String entry : config.getList("standings")) {
            String[] parsed = entry.split(" ");
            UUID relUid = UUID.fromString(parsed[0]);
            double rel = Double.valueOf(parsed[1]);
            standings.put(relUid, rel);
        }
        StoredDataSection effectSection = config.getSection("effects");
        for (String eName : effectSection.getKeys(false)) {
            Effect e = Townships.getEffectManager()
                .loadEffect(eName, this, effectSection.getSection(eName));
            effects.put(eName.toLowerCase(), e);
        }
        if (((ConfigurationSection) requirements.getBackingImplementation())
            .contains("block-requirements")) {
            metadata.put(MetaKeys.REQUIREMENT_BLOCK,
                new RegionBlockCheckTask(this, (TownshipsPlugin) Townships.getInstance()));
        }
        if (((ConfigurationSection) requirements.getBackingImplementation())
            .contains("region-types-min") || ((ConfigurationSection) requirements
            .getBackingImplementation()).contains("region-tiers-min")) {
            metadata.put(MetaKeys.REQUIREMENT_BLOCK,
                new RegionSubregionCheckTask(this, (TownshipsPlugin) Townships.getInstance()));
        }
    }

    @Override public int getTier() {
        return tier;
    }

    @Override public UUID getUid() {
        return regionUid;
    }

    @Override public Optional<String> getName() {
        return Optional.fromNullable(name);
    }

    @Override public Map<FunctionalRegion, RelationState> getRelations() {
        return relations;
    }

    @Override public Map<FunctionalRegion, RelationState> getExternRelations() {
        return externRelations;
    }

    @Override public Collection<UUID> getRole(RoleGroup group) {
        return citizenUidsByRole.get(group);
    }

    @Override public Collection<Citizen> getCitizensInBounds() {
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

    @Override public RegionBoundingArea getBounds() {
        return bounds;
    }

    @Override public Collection<Effect> getEffects() {
        return effects.values();
    }

    @Override public boolean hasEffect(String name) {
        return effects.containsKey(name.toLowerCase());
    }

    @Override public void addEffect(Effect e, boolean persistent) {
        // TODO
    }

    @Override public Effect removeEffect(String name) {
        return null; // TODO
    }

    @Override public <T extends Effect> T getEffect(String name) throws IllegalStateException {
        T ret = (T) effects.get(name.toLowerCase());
        if (ret == null) {
            throw new IllegalStateException("An attempt to retrieve the effect " + name
                + " was made when it did not exist on a region");
        }
        return ret;
    }

    @Override public Collection<RoleGroup> getRoles(Citizen citizen) {
        return rolesByCitizenUid.get(citizen.getUid());
    }

    @Override public void saveConfigs(StoredDataSection data) {
        data.set("name", name);
        data.set("type", type);
        data.set("tier", tier);
        data.set("uid", regionUid.toString());
        data.set("regions-bound-class", getBounds().getClass().getName());
        data.set("regions-bound-settings", getBounds().save().toString());
        StoredDataSection roleSection = data.getSection("roles");
        for (RoleGroup group : citizenUidsByRole.keySet()) {
            List<String> toAdd = citizenUidsByRole.get(group).stream().map(UUID::toString)
                .collect(Collectors.toCollection(LinkedList::new));
            roleSection.set(group.toString(), toAdd);
        }
        StoredDataSection meta = data.getSection("metadata ");
        for (String key : metadata.keySet()) {
            meta.set(key, metadata.get(key));
        }
        StoredDataSection effectSection = data.getSection("effects");
        for (Effect effect : effects.values()) {
            effect.onUnload(Townships.getInstance(), this,
                effectSection.getSection(effect.getName()));
        }

        // Requirements
        StoredDataSection requirements = data.getSection("requirements");
        StoredDataSection maxRegionReqSection = requirements.getSection("region-types-max");
        for (String type : maxTypeInRegion.keySet()) {
            maxRegionReqSection.set(type, maxTypeInRegion.get(type));
        }
        StoredDataSection maxTierReqSection = requirements.getSection("region-tiers-max");
        for (int tierNum : maxTierInRegion.keySet()) {
            maxTierReqSection.set(tierNum + "", maxTierInRegion.get(tierNum));
        }

        // Diplomacy
        StoredDataSection diplomacy = data.getSection("diplomacy");
        StoredDataSection selfDiplomacy = diplomacy.getSection("self");
        StoredDataSection externDiplomacy = diplomacy.getSection("others");
        for (Entry<FunctionalRegion, RelationState> e : relations.entrySet()) {
            selfDiplomacy.set(e.getKey().getUid().toString(), e.getValue().toString());
        }
        for (Entry<FunctionalRegion, RelationState> e : externRelations.entrySet()) {
            externDiplomacy.set(e.getKey().getUid().toString(), e.getValue().toString());
        }
    }

    @Override public String getType() {
        return type;
    }

    public Collection<Area> getBoundingAreas() {
        return containingAreas;
    }

    @Override public void addRole(Citizen citizen, RoleGroup group) {
        rolesByCitizenUid.put(citizen.getUid(), group);
        citizenUidsByRole.put(group, citizen.getUid());
    }

    @Override public boolean removeRole(Citizen citizen, RoleGroup group) {
        boolean ret = rolesByCitizenUid.remove(citizen.getUid(), group);
        boolean ret2 = citizenUidsByRole.remove(group, citizen.getUid());
        return ret || ret2;
    }

    @Override public boolean hasAccess(Citizen citizen,
        AccessType type) { // TODO further investigate if not n^2 algorithm is possible
        if (citizen.isRoot()) {
            return true;
        } else {
            for (AccessType access : accessByCitizenUid.get(citizen.getUid())) {
                if (access.hasAccess(type)) {
                    return true;
                }
            }
            HashSet<RoleGroup> effective = new HashSet<RoleGroup>();
            for (FunctionalRegion r : parents) {
                effective.addAll(r.getRoles(citizen));
                for (RoleGroup group : effective) {
                    if (r.hasAccess(group, type)) {
                        return true;
                    }
                }
            }
            for (RoleGroup group : effective) {
                for (AccessType access : accessByRole.get(group)) {
                    if (access.hasAccess(type)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override public boolean hasAccess(RoleGroup group, AccessType type) {
        for (AccessType access : accessByRole.get(group)) {
            if (access.hasAccess(type)) {
                return true;
            }
        }
        return false;
    }

    @Override public void addEconomyProvider(EconomyProvider provider) {
        economyProviders.put(provider.getIdentifier(), provider);
    }

    @Override public void addItemProvider(ItemProvider provider) {
        itemProviders.put(provider.getIdentifier(), provider);
    }

    @Override public void removeEconomyProvider(EconomyProvider provider) {
        economyProviders.remove(provider.getIdentifier());
    }

    @Override public void removeItemProvider(ItemProvider provider) {
        itemProviders.remove(provider.getIdentifier());
    }

    @Override public Map<String, EconomyProvider> getEconomyProviders() {
        return economyProviders;
    }

    @Override public Map<String, ItemProvider> getItemProviders() {
        return itemProviders;
    }

    @Override public boolean isValid() {
        return valid;
    }

    @Override public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override public Collection<FunctionalRegion> getParents() {
        return parents;
    }

    @Override public Collection<FunctionalRegion> getChildren() {
        return children;
    }

    @Override public boolean isCompatible(FunctionalRegion child) {
        String type = child.getType().toLowerCase();
        int tier = child.getTier();
        boolean careType = maxTypeInRegion.containsKey(type);
        int amtType = maxTypeInRegion.get(type);
        boolean careTier = maxTierInRegion.containsKey(tier);
        int amtTier = maxTierInRegion.get(tier);
        for (FunctionalRegion r : getChildren()) {
            if (careTier && r.getTier() == tier) {
                amtTier--;
            }
            if (careType && r.getType().equalsIgnoreCase(type)) {
                amtType--;
            }
        }
        return !(amtTier <= 0 || amtType <= 0);
    }

    @Override public Map<String, Object> getMetadata() {
        return metadata;
    }

    @Override public Map<UUID, Object> getCompositeMetaValue(String key) {
        Map<UUID, Object> results = new HashMap<>();
        regionMetadata.entrySet().stream().filter(e -> e.getValue().containsKey(key))
            .forEach(e -> results.put(e.getKey(), e.getValue().get(key)));
        return results;
    }

    @Override public Map<String, Object> getRegionalMetadata(FunctionalRegion region) {
        return regionMetadata.getOrDefault(region.getUid(), new HashMap<>());
    }

    @Override public boolean addAccess(RoleGroup group, AccessType access) {
        return accessByRole.put(group, access);
    }

    @Override public boolean removeAccess(RoleGroup group, AccessType access) {
        return accessByRole.remove(group, access);
    }

    @Override public boolean addAccess(UUID uid, AccessType access) {
        return accessByCitizenUid.put(uid, access);
    }

    @Override public boolean removeAccess(UUID uid, AccessType access) {
        return accessByCitizenUid.remove(uid, access);
    }

    @Override public boolean isCitizen(Citizen citizen) {
        return citizens.contains(citizen.getUid());
    }

    @Override public Collection<UUID> getCitizens() {
        return citizens;
    }

    @Override public double updatePower(double amount) {
        currPower += amount; // TODO throw events
        if (currPower < 0.00) {
            currPower = 0.00;
        } else if (currPower > maxPower) {
            currPower = maxPower;
        }
        return currPower;
    }

    @Override public double getMaxPower() {
        return maxPower;
    }

    @Override public RelationState getEffectiveRelation(Citizen citizen) {
        return null;
    }

    @Override public int hashCode() {
        return regionUid.hashCode();
    }

    @Override public boolean equals(Object o) {
        if (o instanceof UUID) {
            return regionUid.equals(o);
        } else {
            return o instanceof FunctionalRegion && ((FunctionalRegion) o).getUid().equals(regionUid);
        }
    }


}
