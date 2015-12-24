package net.kingdomsofarden.townships.api.characters;

import net.kingdomsofarden.townships.api.effects.Effect;
import net.kingdomsofarden.townships.api.permissions.RoleGroup;
import net.kingdomsofarden.townships.api.regions.Area;
import net.kingdomsofarden.townships.api.regions.Region;
import net.kingdomsofarden.townships.api.util.StoredDataSection;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public interface Citizen {

    StoredDataSection getStoredEffectSettings(Effect effect);

    /**
     * @param region The region to get roles for
     * @return The roles held by the given citizen within a region
     */
    Collection<RoleGroup> getRoles(Region region);

    /**
     * @return The smallest bounded area within which the Citizen currently is located
     */
    Area getCurrentArea();

    /**
     * Updates the Citizen to be in the given Area. Should always be terminal in the default implementation
     *
     * @param area The Area to assign to the Citizen
     */
    void setCurrentArea(Area area);

    UUID getUid();

    /**
     * @return True if the citizen has access to and is currently in ROOT role/mode, which grants global access rights
     */
    boolean isRoot();

    /**
     * @return The region/government that citizen is a member of, or null
     */
    Region getCitizenRegion();

    /**
     * @return Regions that the citizen is currently in (used for region entrance/exit events)
     */
    Set<Region> getActiveRegions();

    /**
     * Set the regions that the citizen is currently in
     * @param currActive
     */
    void setActiveRegions(Set<Region> currActive);
}
