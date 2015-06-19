package net.kingdomsofarden.townships.util;

import org.bukkit.Material;

import java.util.Collection;

/**
 * Temporary Constants List to be refactored when Configuration API is done
 */
public class Constants {

    // Performance
    public static int MIN_DIV_X;
    public static int MIN_DIV_Z;
    public static int EFFECT_SPREAD_DELAY;
    public static int BLOCK_CHECK_DELAY;

    // Protection
    public static Collection<Material> INTERACT_TYPES;
    public static Collection<Material> ACCESS_TYPES;
    public static boolean PROTECT_FIRE;

    // Diplomacy
    public static long RELATION_DELAY;
    public static long YEAR_LENGTH;

    // Warring
    public static double POWER_LOSS_PER_DEATH_PVP;
    public static double POWER_LOSS_PER_TICK_OCCUPATION;
    public static double POWER_GAIN_PER_TICK_OCCUPATION;
    public static long PERIOD_OCCUPATION_UPDATE;
}
