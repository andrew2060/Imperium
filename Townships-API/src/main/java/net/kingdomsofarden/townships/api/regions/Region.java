package net.kingdomsofarden.townships.api.regions;

import com.google.common.base.Optional;
import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.effects.Effect;
import net.kingdomsofarden.townships.api.permissions.AccessType;
import net.kingdomsofarden.townships.api.permissions.RoleGroup;
import net.kingdomsofarden.townships.api.regions.bounds.RegionBoundingBox;
import net.kingdomsofarden.townships.api.relations.RelationState;
import net.kingdomsofarden.townships.api.resources.EconomyProvider;
import net.kingdomsofarden.townships.api.resources.ItemProvider;
import net.kingdomsofarden.townships.api.util.StoredDataSection;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a region
 */
public interface Region {

    /**
     * @return The tier of the region, in general lower tiers refers to smaller regions while higher tiers refer to
     * regions that contain lower tiered regions
     */
    int getTier();

    /**
     * @return A unique identifier for this region
     */
    UUID getUid();

    /**
     * @return A unique name for this region, may not be present
     */
    Optional<String> getName();

    /**
     * @return A map of Region, Relation value pairs matching a region to the relation this region currently holds with it
     */
    Map<Region, RelationState> getRelations();

    /**
     * @return A map of Region to Relation States held by other relationable regions toward this region
     */
    Map<Region, RelationState> getExternRelations();

    /**
     * @param group The group to get
     * @return A collection of the UUID of citizens that hold the given role within a region. Note that this collection
     * is not comprehensive as role is also inherited from higher tiered regions that have this region within its borders
     */
    Collection<UUID> getRole(RoleGroup group);

    /**
     * @return A collection of citizens currently within the bounds of this region
     */
    Collection<Citizen> getCitizensInBounds();

    /**
     * @return The {@link RegionBoundingBox} representing this region's area
     */
    RegionBoundingBox getBounds();

    /**
     * @return A collection of effects currently active for this region
     */
    Collection<Effect> getEffects();

    /**
     * @param name The name of the effect, non-case sensitive
     * @return Whether the effect is active on the given region
     */
    boolean hasEffect(String name);

    /**
     * @param <T> The explicit class type of the effect to retrieve
     * @param name The name of the effect non-case sensitive
     * @return The effect retrieved
     * @throws IllegalStateException if no such effect exists
     */
    <T extends Effect> T getEffect(String name) throws IllegalStateException ;

    /**
     * @param citizen The citizen to check
     * @return A collection of roles held by a given citizen
     */
    Collection<RoleGroup> getRoles(Citizen citizen);

    /**
     * Populates the parameter data section with this region's configuration
     * @param data The data section to save to
     */
    void saveConfigs(StoredDataSection data);

    /**
     * @return The type name of the region
     */
    String getType();

    void addRole(Citizen citizen, RoleGroup group);

    boolean removeRole(Citizen citizen, RoleGroup group);

    /**
     * @param citizen The citizen to check
     * @param type The type of access to check for
     * @return true if the citizen has the given or compatible access type
     */
    boolean hasAccess(Citizen citizen, AccessType type);

    /**
     * @param group The group to check
     * @param type The type of access to check for
     * @return true if the group has the given or compatible access type
     */
    boolean hasAccess(RoleGroup group, AccessType type);

    void addEconomyProvider(EconomyProvider provider);

    void addItemProvider(ItemProvider provider);

    void removeEconomyProvider(EconomyProvider provider);

    void removeItemProvider(ItemProvider provider);

    EconomyProvider[] getEconomyProviders();

    ItemProvider[] getItemProviders();

    /**
     * @return The region's validity, i.e. whether it is currently considered a valid region (may not be true if, for
     * instance, the region has been destroyed but not all references have been cleaned up)... this is generally used to
     * check for scheduled effect ticking
     */
    boolean isValid();

    /**
     * Sets the region's validity
     * @param valid The validity state of the region
     * @see #isValid()
     */
    void setValid(boolean valid);

    /**
     * @return A collection of parent regions that encompass this region
     */
    Collection<Region> getParents();

    /**
     * @return A collection of child regions that are encompassed by this region
     */
    Collection<Region> getChildren();

    /**
     * @param region The region to check
     * @return Whether the given region is compatible (i.e. satisfies tier/type restrictions, amongst others) as a
     * parent/child of this region
     */
    boolean isCompatible(Region region);

    /**
     * @return A mapping of metadata values currently associated with the region
     */
    Map<String, Object> getMetadata();

    boolean addAccess(RoleGroup group, AccessType access);
    boolean removeAccess(RoleGroup group, AccessType access);

    boolean addAccess(UUID uid, AccessType access);
    boolean removeAccess(UUID uid, AccessType access);

    /**
     * @param player The player to check
     * @return Whether the given Citizen is a Citizen of this (super) region
     */
    boolean isCitizen(Citizen player);
}
