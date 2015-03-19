package net.kingdomsofarden.townships.api.characters;

import net.kingdomsofarden.townships.api.regions.Area;

public interface Citizen {
    /**
     * @return The smallest bounded area within which the Citizen currently is located
     */
    Area getCurrentArea();

    /**
     * Updates the Citizen to be in the given Area. Should always be terminal in the default implementation
     * @param area The Area to assign to the Citizen
     */
    void setCurrentArea(Area area);
}
