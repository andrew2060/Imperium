package net.kingdomsofarden.townships.api.regions;

import com.google.common.base.Optional;
import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.effects.Effect;
import net.kingdomsofarden.townships.api.permissions.RoleGroup;
import net.kingdomsofarden.townships.api.regions.bounds.RegionBoundingBox;
import net.kingdomsofarden.townships.api.util.StoredDataSection;

import java.util.Collection;
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
}
