package net.kingdomsofarden.townships.util;

import org.bukkit.Material;

import java.util.Collection;
import java.util.HashSet;

/**
 * Temporary Constants List to be refactored when Configuration API is done
 */
public class Constants {
    public static final int MIN_DIV_X = 100;
    public static final int MIN_DIV_Z = 100;

    public static final int EFFECT_SPREAD_DELAY = 5;
    public static final Collection<Material> INTERACT_TYPES;
    public static final Collection<Material> ACCESS_TYPES;

    static {
        INTERACT_TYPES = new HashSet<Material>();
        INTERACT_TYPES.add(Material.ACACIA_DOOR);
        INTERACT_TYPES.add(Material.ACACIA_FENCE_GATE);
        INTERACT_TYPES.add(Material.BIRCH_DOOR);
        INTERACT_TYPES.add(Material.BIRCH_FENCE_GATE);
        INTERACT_TYPES.add(Material.DARK_OAK_DOOR);
        INTERACT_TYPES.add(Material.DARK_OAK_FENCE_GATE);
        INTERACT_TYPES.add(Material.DRAGON_EGG);
        INTERACT_TYPES.add(Material.DIODE_BLOCK_OFF);
        INTERACT_TYPES.add(Material.DIODE_BLOCK_ON);
        INTERACT_TYPES.add(Material.FENCE_GATE);
        INTERACT_TYPES.add(Material.JUNGLE_DOOR);
        INTERACT_TYPES.add(Material.JUNGLE_FENCE_GATE);
        INTERACT_TYPES.add(Material.LEVER);
        INTERACT_TYPES.add(Material.NOTE_BLOCK);
        INTERACT_TYPES.add(Material.PAINTING);
        INTERACT_TYPES.add(Material.REDSTONE_COMPARATOR_OFF);
        INTERACT_TYPES.add(Material.REDSTONE_COMPARATOR_ON);
        INTERACT_TYPES.add(Material.SIGN);
        INTERACT_TYPES.add(Material.SIGN_POST);
        INTERACT_TYPES.add(Material.SPRUCE_DOOR);
        INTERACT_TYPES.add(Material.SPRUCE_FENCE_GATE);
        INTERACT_TYPES.add(Material.STONE_BUTTON);
        INTERACT_TYPES.add(Material.TRAP_DOOR);
        INTERACT_TYPES.add(Material.WALL_SIGN);
        INTERACT_TYPES.add(Material.WOOD_BUTTON);
        INTERACT_TYPES.add(Material.WOODEN_DOOR);

        ACCESS_TYPES = new HashSet<Material>();
        ACCESS_TYPES.add(Material.ANVIL);
        ACCESS_TYPES.add(Material.BREWING_STAND);
        ACCESS_TYPES.add(Material.CHEST);
        ACCESS_TYPES.add(Material.DISPENSER);
        ACCESS_TYPES.add(Material.DROPPER);
        ACCESS_TYPES.add(Material.ENCHANTMENT_TABLE);
        ACCESS_TYPES.add(Material.ENDER_CHEST);
        ACCESS_TYPES.add(Material.ENDER_PORTAL_FRAME);
        ACCESS_TYPES.add(Material.FURNACE);
        ACCESS_TYPES.add(Material.BURNING_FURNACE);
        ACCESS_TYPES.add(Material.HOPPER);
        ACCESS_TYPES.add(Material.ITEM_FRAME);
        ACCESS_TYPES.add(Material.JUKEBOX);
        ACCESS_TYPES.add(Material.TRAPPED_CHEST);
        ACCESS_TYPES.add(Material.WORKBENCH);

    }

    public static boolean PROTECT_FIRE = true;
}
