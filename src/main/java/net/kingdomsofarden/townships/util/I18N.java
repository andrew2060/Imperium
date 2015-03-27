package net.kingdomsofarden.townships.util;

import org.bukkit.ChatColor;

/**
 * Message format strings
 */
public class I18N {
    public static final String GLOBAL_PREFIX = ChatColor.AQUA + "[Townships]" + ChatColor.GRAY;
    public static final String NO_PERMISSION_AREA_CONSTRUCT = GLOBAL_PREFIX + "You lack the necessary permissions to " +
            "construct at this location.";
    public static final String NO_PERMISSION_AREA_INTERACT = GLOBAL_PREFIX + "You lack the necessary permissions to " +
            "interact with this object at this location.";
    public static final String NO_PERMISSION_AREA_ACCESS = GLOBAL_PREFIX + "You lack the necessary permissions to " +
            "access this container.";
}
