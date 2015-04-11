package net.kingdomsofarden.townships.effects;

import net.kingdomsofarden.townships.api.effects.TickableEffect;
import net.kingdomsofarden.townships.api.regions.Region;

import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.UUID;

/**
 * Handles scheduling and load balancing for Region ticking
 */
public class EffectTaskManager implements Runnable {
    private EffectTaskStack[] taskStacks;
    private int[] minSizeHeap; //Tracks the task stack with the smallest load, we keep this seperate due to consistancy
    private HashMap<UUID, Integer> heapMapping; // Tracks the stack to which a region with a given UID belongs
    private EffectHeap cooldownHeap;
    private HashSet<TickableEffect> toRemove;
    private int tickDelay;


    private int sentinel; // Tracks which section we are ticking

    public EffectTaskManager(int width) {
        cooldownHeap = new EffectHeap();
        taskStacks = new EffectTaskStack[width];
        minSizeHeap = new int[width];
        for (int i = 0; i < width; i++) {
            minSizeHeap[i] = i;
        }
        sentinel = 0;
        tickDelay = width;
        toRemove = new HashSet<TickableEffect>();
    }

    /**
     * Schedules the effect for ticking
     * @param tickableEffect The effect to tick
     * @param scheduling The Region that is scheduling this effect
     */
    public void schedule(TickableEffect tickableEffect, Region scheduling) {
        if (toRemove.contains(tickableEffect)) { // Cancel removal if we subsequently schedule again
            toRemove.remove(tickableEffect);
        }
        EffectTask effectTask = new EffectTask(scheduling, tickableEffect);
        cooldownHeap.add(effectTask);
    }

    /**
     * Schedules the effect for removal before its next tick
     * Note: double check that the effect you are scheduling for removal is actually scheduled in the first place, otherwise
     * memory leakage will occur (this check is not performed automatically due to performance concerns)
     * @param effect The effect to unschedule
     */
    public void unschedule(TickableEffect effect) {
        toRemove.add(effect);
    }


    // Called upon ticking
    public void run() {
        Deque<EffectTask> tickable = new LinkedList<EffectTask>(); // Get available
        long tickTime = System.currentTimeMillis();
        while (cooldownHeap.get().getNextTick() <= tickTime) {
            EffectTask task = cooldownHeap.removeFirst();
            if (toRemove.contains(task.getTickable())) {
                toRemove.remove(task.getTickable());
            } else {
                tickable.add(task);
            }
        }
        for (EffectTask task : tickable) { // Schedule
            task.setScheduledTime(tickTime);
            EffectTaskStack stack = taskStacks[minSizeHeap[0]];
            if (stack == null) {
                stack = new EffectTaskStack(0);
                taskStacks[minSizeHeap[0]] = stack;
            }
            stack.add(task);
            percolateDown(0);
        }
        EffectTaskStack stack = taskStacks[sentinel];
        EffectTask task;
        while (stack != null && (task = stack.pollLast()) != null) {
            task.tick();
            if (task.isReschedulable()) {
                cooldownHeap.add(task);
            }
        }
        if (stack != null) {
            percolateUp(stack.getPosition());
        }
        sentinel++;
        if (sentinel == minSizeHeap.length) {
            sentinel = 0;
        }
    }

    // Heap stuff

    private void swap(int orig, int swap) {
        int temp = minSizeHeap[orig];
        minSizeHeap[orig] = minSizeHeap[swap];
        minSizeHeap[swap] = temp;
        if (taskStacks[minSizeHeap[orig]] != null) {
            taskStacks[minSizeHeap[orig]].setPosition(orig);
        }
        if (taskStacks[minSizeHeap[swap]] != null) {
            taskStacks[minSizeHeap[swap]].setPosition(swap);
        }
    }

    private void percolateUp(int idx) {
        if (idx == 0) {
            return;
        }
        int parent = (idx - 1) / 2;
        long parentLoad = taskStacks[minSizeHeap[parent]].getLoad();
        long currLoad = taskStacks[minSizeHeap[idx]].getLoad();
        if (currLoad < parentLoad) {
            swap(parent, idx);
            percolateUp(parent);
        }
    }

    private void percolateDown(int idx) {
        boolean isHeaped = false;
        int rootIdx = minSizeHeap[idx];
        int childIdx;
        long rootLoad = taskStacks[rootIdx] == null ? -1 : taskStacks[rootIdx].getLoad();
        while (!isHeaped && (childIdx = 2 * rootIdx + 1) < minSizeHeap.length) {
            int leftIdx = minSizeHeap[childIdx];
            double childLoad = taskStacks[leftIdx] == null ? -1 : taskStacks[leftIdx].getLoad();
            if (childIdx + 1 < minSizeHeap.length) { // Two children, compare
                int rightIdx = minSizeHeap[childIdx + 1];
                double rightLoad = taskStacks[rightIdx] == null ? -1 : taskStacks[rightIdx].getLoad();
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
