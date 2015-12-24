package net.kingdomsofarden.townships.effects;

import java.util.Stack;

/**
 * Binary tree implementation of an effect heap, we do not use an array due to constant changing sizes
 */
public class EffectHeap {

    private static EffectHeap root = null;
    private static EffectHeap last = null; // Pointer for use with min deletions only
    private static int size = 0;

    private EffectTask value = null;
    private EffectHeap left = null;
    private EffectHeap right = null;
    private EffectHeap parent = null;

    private int direction = -1; // 0 if left child, 1 if right child

    private static void swap(EffectHeap e1, EffectHeap e2) {
        EffectTask temp = e1.value;
        e1.value = e2.value;
        e2.value = temp;
    }

    public EffectTask get() {
        return root.value;
    }

    public void add(EffectTask task) {
        if (root == null) {
            root = new EffectHeap();
            root.value = task;
            last = root;
            size++;
        } else {
            // Select complete path
            Stack<Integer> pathStack = new Stack<Integer>();
            int add = size + 1; // The size of the tree post-selection
            while (add > 1) { // Generate path
                pathStack.push(add % 2);
                add /= 2;
            }
            // Tree traversal
            EffectHeap curr = root;
            while (pathStack.size() > 1) {
                int dir = pathStack.pop();
                if (dir == 0) {
                    curr = curr.left;
                } else {
                    curr = curr.right;
                }
            }
            // Insertion
            if (pathStack.pop() == 0) {
                curr.left = new EffectHeap();
                curr.left.direction = 0;
                curr.left.parent = curr;
                curr = curr.left;
            } else {
                curr.right = new EffectHeap();
                curr.right.parent = curr;
                curr.right.direction = 1;
                curr = curr.right;
            }
            curr.value = task;
            last = curr;
            size++;
            // Heapify
            percolateUp(last);
        }
    }

    public EffectTask removeFirst() {
        if (root == null) {
            return null;
        } else {
            swap(root, last);
            EffectTask ret = last.value;
            EffectHeap temp = last.parent;
            if (last.direction == 1) {
                temp.right = null;
                last.value = null;
                last = temp.left; // Let GC clear the other fields
                percolateDown(root);
                size--;
                return ret;
            } else {
                last.value = null;
                temp.left = null;
                size--;
                // Update last
                // Select complete path
                Stack<Integer> pathStack = new Stack<Integer>();
                int add = size; // The size of the tree post-selection
                while (add > 1) { // Generate path
                    pathStack.push(add % 2);
                    add /= 2;
                }
                EffectHeap curr = root;
                while (!pathStack.isEmpty()) {
                    int dir = pathStack.pop();
                    if (dir == 0) {
                        curr = curr.left;
                    } else {
                        curr = curr.right;
                    }
                }
                last = curr;
                return ret;
            }
        }
    }

    private void percolateDown(EffectHeap node) {
        if (node.left != null) { // node.left will always be non-null before node.right is
            EffectHeap curr = node.left;
            if (node.right != null) {
                if (node.right.value.getNextTick() < node.left.value.getNextTick()) {
                    curr = node.right;
                }
            }
            if (curr.value.getNextTick() < node.value.getNextTick()) {
                swap(curr, node);
                percolateDown(curr);
            }
        }
    }

    private void percolateUp(EffectHeap node) {
        if (node.parent == null) {
            return;
        }
        long val = node.value.getNextTick();
        if (node.parent.value.getNextTick() > val) {
            swap(node, node.parent);
            percolateUp(node.parent);
        }
    }

    public EffectHeap getLeft() {
        return left;
    }

    public void setLeft(EffectHeap left) {
        this.left = left;
    }

    public EffectHeap getRight() {
        return right;
    }

    public void setRight(EffectHeap right) {
        this.right = right;
    }

    public EffectHeap getParent() {
        return parent;
    }

    public void setParent(EffectHeap parent) {
        this.parent = parent;
    }
}
