package net.kingdomsofarden.townships.api.characters;

import java.util.Collection;
import java.util.UUID;

public interface CitizenManager extends Collection<Citizen> {
    /**
     * @param id The UUID of the Citizen (Player UID)
     * @return The corresponding Citizen
     */
     Citizen getCitizen(UUID id);
}
