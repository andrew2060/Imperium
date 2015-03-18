package net.kingdomsofarden.townships.api;

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
     * Updates the currently running instance
     * @param instance The instance of the plugin
     */
    public static void setInstance(ITownshipsPlugin instance) {
        Townships.i = i;
    }
}
