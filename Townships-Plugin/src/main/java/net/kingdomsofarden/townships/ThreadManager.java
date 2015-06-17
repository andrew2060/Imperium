package net.kingdomsofarden.townships;

import java.util.concurrent.ExecutorService;

public class ThreadManager {
    private static ThreadManager INSTANCE;

    private ExecutorService threadPool;

    public static ThreadManager getInstance() {
        return INSTANCE;
    }

    public ExecutorService getThreadPool() {
        return threadPool;
    }
}
