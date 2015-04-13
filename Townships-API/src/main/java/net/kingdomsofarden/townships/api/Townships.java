package net.kingdomsofarden.townships.api;

import net.kingdomsofarden.townships.api.characters.CitizenManager;
import net.kingdomsofarden.townships.api.configuration.Configuration;
import net.kingdomsofarden.townships.api.effects.EffectManager;
import net.kingdomsofarden.townships.api.regions.RegionManager;

/**
 * Non-extensible class that contains methods to interact with the underlying plugin
 */
public final class Townships {
    private static ITownshipsPlugin i;

    /**
     * @return The active {@link RegionManager}, which is also a collection and can be interacted with as such
     */
    public static RegionManager getRegions() {
        return i.getRegions();
    }

    /**
     * @return The active {@link CitizenManager}, which is also a collection and can be interacted with as such
     */
    public static CitizenManager getCitizens() {
        return i.getCitizens();
    }

    /**
     * Updates the currently running instance
     * @param instance The instance of the plugin
     */
    public static void setInstance(ITownshipsPlugin instance) {
        Townships.i = instance;
    }

    public static ITownshipsPlugin getInstance() {
        return i;
    }

    public static EffectManager getEffectManager() {
        return i.getEffectManager();
    }

    public static Configuration getConfiguration() { return i.getConfiguration(); }
}
