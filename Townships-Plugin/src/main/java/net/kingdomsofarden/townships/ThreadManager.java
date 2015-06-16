package net.kingdomsofarden.townships;

import java.util.concurrent.ExecutorService;

public class ThreadManager {
    private static ThreadManager INSTANCE;

    private ExecutorService regionThreadPool;

    public static ThreadManager getInstance() {
        return INSTANCE;
    }

    public ExecutorService getRegionThreadPool() {
        return regionThreadPool;
    }
}
