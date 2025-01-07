package disjointsets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A quick-union-by-size data structure with path compression.
 * @see DisjointSets for more documentation.
 */
public class UnionBySizeCompressingDisjointSets<T> implements DisjointSets<T> {
    // Do NOT rename or delete this field. We will be inspecting it directly in our private tests.
    List<Integer> pointers;
    // Pointers holds the Disjoint array representation. Representatives have negative values
    HashMap<T, Integer> map;
    // Map takes items as keys and returns the appropriate pointers index that item is located
    Integer size;

    /*
    However, feel free to add more fields and private helper methods. You will probably need to
    add one or two more fields in order to successfully implement this class.
    */

    public UnionBySizeCompressingDisjointSets() {
        this.pointers = new ArrayList<>();
        this.map = new HashMap<>();
        this.size = 0;
    }

    @Override
    public void makeSet(T item) {
        this.pointers.add(size, -1);
        map.put(item, size);
        this.size++;
    }

    @Override
    public int findSet(T item) {
        if (!map.containsKey(item)) {
            throw new IllegalArgumentException();
        }
        int index = map.get(item);
        int value = pointers.get(index);
        ArrayList<Integer> tempIndex = new ArrayList<>(size);
        while (value >= 0) {
            tempIndex.add(index);
            index = value;
            value = pointers.get(index);
        }
        for (int i = 0; i < tempIndex.size(); i++) {
            int temp = tempIndex.get(i);
            pointers.set(temp, index);
        }
        return index;
    }

    @Override
    public boolean union(T item1, T item2) {
        if (!map.containsKey(item1) || !map.containsKey(item2)) {
            throw new IllegalArgumentException();
        }
        int index1 = findSet(item1); //rep index
        int item1rep = pointers.get(index1); // rep size
        int index2 = findSet(item2);
        int item2rep = pointers.get(index2);

        // if representitives do not have the same index
        if (index1 != index2) {
            int newSize = item2rep + item1rep;
            if (item2rep < item1rep) {
                pointers.set(index1, index2);
                pointers.set(index2, newSize);
            } else {
                pointers.set(index2, index1);
                pointers.set(index1, newSize);
            }
            return true;
        }
        return false;
    }
}
