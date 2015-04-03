package net.kingdomsofarden.townships.command.selection;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.kingdomsofarden.townships.regions.bounds.CuboidSelection;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SelectionManager {
    public static LoadingCache<UUID, CuboidSelection> selections;

    static {
        selections = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build(new CacheLoader<UUID, CuboidSelection>() {
            @Override
            public CuboidSelection load(UUID key) throws Exception {
                return new CuboidSelection();
            }
        });
    }
}
