package net.kingdomsofarden.townships.command.selection;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SelectionManager {
    public static LoadingCache<UUID, Selection> selections;

    static {
        selections = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build(new CacheLoader<UUID, Selection>() {
            @Override
            public Selection load(UUID key) throws Exception {
                return new Selection();
            }
        });
    }
}
