package net.kingdomsofarden.townships.api.resources;

import org.bukkit.Material;

public interface ResourceProvider {
    /**
     * @param mat The material to check
     * @return The amount of said material present
     */
    int getAmount(Material mat);

    /**
     * Attempts to remove up to max amount of mat (depending on availability) from this provider
     * @param mat The material to remove
     * @param max The maximum amount to remove
     * @return The actual amount removed
     */
    int remove(Material mat, int max);
}
