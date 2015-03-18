package net.kingdomsofarden.townships.api.regions;

import com.google.common.base.Optional;
import net.kingdomsofarden.townships.api.characters.Citizen;
import net.kingdomsofarden.townships.api.util.BoundingBox;
import org.bukkit.Location;

import java.util.Collection;
import java.util.UUID;

/**
 * Represents a region
 */
public interface Region {

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
     * @return The center point of the given region
     */
    Location getLocation();

    /**
     * @return The {@link net.kingdomsofarden.townships.api.util.BoundingBox} representing this region's area
     */
    BoundingBox getBounds();



}
