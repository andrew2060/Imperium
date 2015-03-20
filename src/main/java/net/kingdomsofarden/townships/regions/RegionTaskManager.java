package net.kingdomsofarden.townships.regions;

import net.kingdomsofarden.townships.regions.collections.RegionTaskStack;
import net.kingdomsofarden.townships.util.Constants;

import java.util.Deque;
import java.util.HashMap;
import java.util.UUID;

/**
 * Handles scheduling and load balancing for Region ticking
 */
public class RegionTaskManager implements Runnable {
    private RegionTaskStack[] taskStacks;
    private int[] minSizeHeap; //Tracks the task stack with the smallest load, we keep this seperate due to consistancy
    private HashMap<UUID, Integer> heapMapping; // Tracks the stack to which a region with a given UID belongs
    private Deque<TownshipsRegion> addQueue; // Temporary queue for safe addition/scheduling
    private Deque<TownshipsRegion> removeQueue; // Temporary queue for safe removal/unscheduling

    private int sentinel; // Tracks which section we are ticking
    private int fullRebuild; // Tracks how often to perform a full rebuild

    public RegionTaskManager(int width) {
        taskStacks = new RegionTaskStack[width - 1]; // Last tick used for administrative/balancing
        minSizeHeap = new int[width - 1];
        for (int i = 0; i < width - 1; i++) {
            minSizeHeap[i] = i;
        }
        sentinel = 0;
        fullRebuild = 1;
    }

    /**
     * Immediately starts the first tick and schedules the region for ticking at the beginning of the next tick cycle
     * @param region
     */
    public void schedule(TownshipsRegion region) {
        region.tick(true);
        addQueue.add(region);
    }

    /**
     * Schedules the region for removal at the end of the at the beginning of the next tick cycle
     * @param region The region to unschedule
     */
    public void unschedule(TownshipsRegion region) {
        removeQueue.add(region);
    }


    // Called upon ticking
    public void run() {
        if (sentinel == minSizeHeap.length) {
            if (fullRebuild % Constants.FULL_REBUILD_PER_X_REGION_TICK_CYCLES == 0) { // Conduct a full rebuild
                fullRebuild = 1;
                heapMapping.clear();
                for (RegionTaskStack s : taskStacks) {
                    addQueue.add(s.remove());
                }
            }
            // Process addition queue
            for (TownshipsRegion r : addQueue) {
                int minIdx = minSizeHeap[0];
                if (taskStacks[minIdx] == null) {
                    taskStacks[minIdx] = new RegionTaskStack();
                }
                taskStacks[minIdx].add(r);
                heapMapping.put(r.getUid(), minIdx);
                percolateDown(0);
            }
            // Process removal queue
            for (TownshipsRegion r : removeQueue) {
                int mapping = heapMapping.remove(r.getUid());
                taskStacks[mapping].remove(r);
            }
            rebuildHeap();

            sentinel = 0;
        } else {
            if (taskStacks[sentinel] != null) {
                taskStacks[sentinel].tick(true);
            }
            sentinel++;
        }
    }

    // Heap stuff

    private void swap(int orig, int swap) {
        int temp = minSizeHeap[orig];
        minSizeHeap[orig] = minSizeHeap[swap];
        minSizeHeap[swap] = temp;
    }

    private void rebuildHeap() {
        for (int i = (minSizeHeap.length - 1)/2; i >= 0; i--) { //Rebuild subtrees bottom up
            percolateDown(i);
        }
    }

    private void percolateDown(int idx) {
        boolean isHeaped = false;
        int rootIdx = idx;
        int childIdx;
        double rootLoad = taskStacks[rootIdx] == null ? -1 : taskStacks[rootIdx].getLoadFactor();
        while (!isHeaped && (childIdx = 2 * rootIdx + 1) < minSizeHeap.length) {
            int leftIdx = minSizeHeap[childIdx];
            double childLoad = taskStacks[leftIdx] == null ? -1 : taskStacks[leftIdx].getLoadFactor();
            if (childIdx + 1 < minSizeHeap.length) { // Two children, compare
                int rightIdx = minSizeHeap[childIdx + 1];
                double rightLoad = taskStacks[rightIdx] == null ? -1 : taskStacks[rightIdx].getLoadFactor();
                if (rightLoad < childLoad) { // Check smaller of two to parent
                    childIdx++;
                    childLoad = rightLoad;
                }
            }
            if (childLoad < rootLoad) { // Parent is larger
                swap(rootIdx, childIdx); // Swap values of parent/child
                rootIdx = childIdx; // Check swapped subtree
            } else {
                isHeaped = true;
            }
        }

    }

}
