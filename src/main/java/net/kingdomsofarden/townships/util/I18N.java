package net.kingdomsofarden.townships.util;

import org.bukkit.ChatColor;

/**
 * Message format strings
 */
public class I18N {
    public static final String GLOBAL_PREFIX = ChatColor.AQUA + "[Townships]" + ChatColor.GRAY;
    public static final String NO_PERMISSION_AREA_CONSTRUCT = "You lack the necessary permissions to " +
            "construct at this location.";
    public static final String NO_PERMISSION_AREA_INTERACT = "You lack the necessary permissions to " +
            "interact with this object at this location.";
    public static final String NO_PERMISSION_AREA_ACCESS =  "You lack the necessary permissions to " +
            "access this container.";
    public static final String NO_PERMISSION_COMMAND = "You lack the permission $0, which is needed to " +
            "execute this command.";
    public static final String COMMAND_NOT_FOUND = "No matching command could be found";
    public static final String COMMAND_IMPROPER_ARGUMENTS = "Correct usage for this command: $0";
    public static final String PLAYER_ONLY_COMMAND = "This command can only be executed by players";
    public static final String REGION_TYPE_NOT_FOUND = "Could not find a matching region type for $0.";
    public static final String INVALID_REGION_CONFIGURATION = "The configuration for the region type $0 contains incorrect " +
            "or missing data, please contact your server administrator";
    public static final String INTERNAL_ERROR = "An internal error has occurred, please contact your server administrator";
    public static final String SELECTION_REQUIRED = "You must have a valid active selection prior to running this command";
    public static final String SELECTION_TOO_LARGE = "The size of your selection is larger than the maximum allowable size of " +
            "x=$0, y=$1, z=$2";
    public static final String SELECTION_TOO_SMALL = "The size of your selection is smaller than the minimal allowable size of " +
            "x=$0, y=$1, z=$2";
    public static final String INSUFFICIENT_REQUIREMENT_BLOCKS = "One or more block requirements for this region are not met";
    public static final String INSUFFICIENT_REQUIREMENT_REGION_TYPE = "One or more region types required for this region are not met";
    public static final String INSUFFICIENT_REQUIREMENT_REGION_TIER = "This selection does not contain sufficient numbers of a certain tier of regions";
    public static final String REGION_COLLISION_MUTEX_FAIL = "This selection contains one or more preexisting regions when none are allowed for the requested region type";
    public static final String REGION_COLLISION_GENERAL = "This selection contains one or more preexisting regions that are not compatible with the requested region type";
    public static final String REGION_SUCCESSFULLY_CREATED = "The region was successfully created with id $0";
    public static final String SELECTION_ENABLED = "Selection mode enabled";
    public static final String SELECTION_DISABLED = "Selection mode disabled";
    public static final String SELECTION_SET = "Selection Point $0 set to x:$1 y:$2 z:$3";
    public static final String INVALID_USAGE = "Improper usage of command: correct usage is $0";
    public static final String REGION_NOT_FOUND = "The requested region $0 was not found";
    public static final String PLAYER_NOT_FOUND = "The requested player $0 was not found";
    public static final String COMMAND_COMPLETED_SUCCESSFULLY = "The requested operation was completed";
    public static final String ROLE_NOT_PRESENT = "The requested player $0 does not hold the $1 role in region $2";
    public static final String NO_PERMISSION_AREA_GOVERN = "You lack the required access level to administrate this region";
    public static final String NO_PERMISSION_AREA_ZONING = "You lack the required access level to create/destroy regions in this area";
    public static final String LOWER_TIER_MUST_BE_ENCOMPASSED = "Higher tier regions must completely encompass lower tiered regions";
}
