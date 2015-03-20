package net.kingdomsofarden.townships.util;

/**
 * Temporary Constants List to be refactored when Configuration API is done
 */
public class Constants {
    public static final int MIN_DIV_X = 100;
    public static final int MIN_DIV_Z = 100;

    public static final int TICK_REGIONS_PER_SERVER_TICKS = 40;
    public static final int MAX_PROFILING_LENGTH_MS = 600000;

    public static final int MAX_PROFILING_LENGTH_REGION_TICKS = MAX_PROFILING_LENGTH_MS / (TICK_REGIONS_PER_SERVER_TICKS/20 * 1000);
    public static final int FULL_REBUILD_PER_X_REGION_TICK_CYCLES = 150;
}
