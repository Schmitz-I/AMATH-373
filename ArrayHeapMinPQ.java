package priorityqueues;

import java.util.ArrayList;
import java.util.HashMap;
// import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @see ExtrinsicMinPQ
 */
public class ArrayHeapMinPQ<T> implements ExtrinsicMinPQ<T> {
    // IMPORTANT: Do not rename these fields or change their visibility.
    // We access these during grading to test your code.
    static final int START_INDEX = 0;
    // backIndex is the next open index in array list items
    int backIndex;
    int size;
    List<PriorityNode<T>> items;

    HashMap<T, Integer> itemsMap;

    public ArrayHeapMinPQ() {
        items = new ArrayList<>();
        itemsMap = new HashMap<>();
        backIndex = 0;
        size = 0;
    }

    // Here's a method stub that may be useful. Feel free to change or remove it, if you wish.
    // You'll probably want to add more helper methods like this one to make your code easier to read.
    /**
     * A helper method for swapping the items at two indices of the array heap.
     */
    private void swap(int a, int b) {
        PriorityNode<T> temp = items.get(a);
        T itemA = items.get(a).getItem();
        T itemB = items.get(b).getItem();
        items.set(a, items.get(b));
        items.set(b, temp);
        itemsMap.put(itemA, b);
        itemsMap.put(itemB, a);
    }

    @Override
    public void add(T item, double priority) {
        PriorityNode<T> newNode = new PriorityNode<>(item, priority);
        // HashSet is used to avoid adding the same element twice
        if (itemsMap.containsKey(item)) {
            throw new IllegalArgumentException();
        }

        while (backIndex < START_INDEX) {
            PriorityNode<T> frontNode = new PriorityNode<>(null, 0);
            items.add(backIndex, frontNode);
            backIndex++;
        }
        items.add(backIndex, newNode);
        itemsMap.put(item, backIndex);
        size++;
        if (backIndex > START_INDEX) {
            percolateUp(backIndex);
        }
        backIndex++;
        // all items need to be unique! does this need a unique item checker?
    }

    private void percolateUp(int index) {
        int parentIndex = (index + START_INDEX - 1) / 2;
        double parentPrior = items.get(parentIndex).getPriority();
        double priority = items.get(index).getPriority();
        if (parentPrior > priority) {
            swap(index, parentIndex);
            percolateUp(parentIndex);
        }
    }

    private void percolateDown(int index) {
        int leftChildIndex = (2 * index - START_INDEX + 1);
        int rightChildIndex = (2 * index - START_INDEX + 2);
        // Check if there is a right and left child of curr node
        boolean leftExists = false;
        boolean rightExists = false;
        double leftChildPrior = 0;
        double rightChildPrior = 0;
        if (rightChildIndex < items.size()) {
            rightExists = true;
            leftExists = true;
            rightChildPrior = items.get(rightChildIndex).getPriority();
            leftChildPrior = items.get(leftChildIndex).getPriority();
        } else if (leftChildIndex < items.size()) {
            leftExists = true;
            leftChildPrior = items.get(leftChildIndex).getPriority();
        }
        double priority = items.get(index).getPriority();
        // check whether left and right priority exist, and whether to swap with parent
        if (rightExists) {
            // check left, right, and curr priority
            if (rightChildPrior < leftChildPrior) {
                if (rightChildPrior < priority) {
                    swap(rightChildIndex, index);
                    percolateDown(rightChildIndex);
                }
            } else {
                if (leftChildPrior < priority) {
                    swap(leftChildIndex, index);
                    percolateDown(leftChildIndex);
                }
            }
        } else if (leftExists) {
            if (leftChildPrior < priority) {
                swap(leftChildIndex, index);
            }
        }
    }
    @Override
    public boolean contains(T item) {
        return itemsMap.containsKey(item);
    }

    @Override
    public T peekMin() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        return items.get(START_INDEX).getItem();
    }

    @Override
    public T removeMin() {
        T minItem = peekMin();
        itemsMap.remove(minItem);
        backIndex--;
        size--;
        PriorityNode<T> lastNode = items.get(backIndex);
        items.set(START_INDEX, lastNode);
        items.remove(backIndex);
        if (size > 1) {
            percolateDown(START_INDEX);
        }
        return minItem;
    }

    @Override
    public void changePriority(T item, double priority) {
        if (!contains(item)) {
            throw new NoSuchElementException();
        }
        int index = itemsMap.get(item);
        if (index >= 0 && index < items.size()) {
            double priorPrior = items.get(index).getPriority();
            items.get(index).setPriority(priority);
            if (priorPrior < priority) {
                percolateDown(index);
            } else if (priorPrior > priority) {
                percolateUp(index);
            }
        }
    }

    @Override
    public int size() {
        return size;
    }
}
