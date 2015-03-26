package net.kingdomsofarden.townships.api.regions;

import com.google.common.base.Optional;
import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.effects.Effect;
import net.kingdomsofarden.townships.api.util.BoundingBox;
import org.bukkit.Location;

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
     * @return The owners of a given region (i.e. those with administrative rights over it)
     */
    Collection<Citizen> getOwners();

    /**
     * @return The citizens of a given region (includes owners)
     */
    Collection<Citizen> getCitizens();

    /**
     * @return A collection of citizens currently within the bounds of this region
     */
    Collection<Citizen> getCitizensInBounds();

    /**
     * @return The center point of the given region
     */
    Location getLocation();

    /**
     * @return The {@link net.kingdomsofarden.townships.api.util.BoundingBox} representing this region's area
     */
    BoundingBox getBounds();

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
    <T extends Effect> Effect getEffect(String name) throws IllegalStateException ;
}
