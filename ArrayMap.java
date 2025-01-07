package maps;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * @see AbstractIterableMap
 * @see Map
 */
public class ArrayMap<K, V> extends AbstractIterableMap<K, V> {
    private static final int DEFAULT_INITIAL_CAPACITY = 6;
    /*
    Warning:
    You may not rename this field or change its type.
    We will be inspecting it in our secret tests.
     */
    SimpleEntry<K, V>[] entries;
    private int size;

    // You may add extra fields or helper methods though!

    /**
     * Constructs a new ArrayMap with default initial capacity.
     */
    public ArrayMap() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    /**
     * Constructs a new ArrayMap with the given initial capacity (i.e., the initial
     * size of the internal array).
     *
     * @param initialCapacity the initial capacity of the ArrayMap. Must be > 0.
     */
    public ArrayMap(int initialCapacity) {
        this.entries = this.createArrayOfEntries(initialCapacity);
        size = 0;
    }

    /**
     * This method will return a new, empty array of the given size that can contain
     * {@code Entry<K, V>} objects.
     *
     * Note that each element in the array will initially be null.
     *
     * Note: You do not need to modify this method.
     */
    @SuppressWarnings("unchecked")
    private SimpleEntry<K, V>[] createArrayOfEntries(int arraySize) {
        /*
        It turns out that creating arrays of generic objects in Java is complicated due to something
        known as "type erasure."

        We've given you this helper method to help simplify this part of your assignment. Use this
        helper method as appropriate when implementing the rest of this class.

        You are not required to understand how this method works, what type erasure is, or how
        arrays and generics interact.
        */
        return (SimpleEntry<K, V>[]) (new SimpleEntry[arraySize]);
    }

    @Override
    public V get(Object key) {
        for (int i = 0; i < size; i++) {
            if (Objects.equals(entries[i].getKey(), key)) {
                return entries[i].getValue();
            }
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        for (int i = 0; i < size; i++) {
            if (Objects.equals(entries[i].getKey(), key)) {
                V prevVal = entries[i].getValue();
                entries[i].setValue(value);
                return prevVal;
            }
        }
        entries[size] = new SimpleEntry<>(key, value);
        size++;
        if (size == entries.length) {
            resize();
        }
        return null;
    }

    private void resize() {
        if (size == 0) {
            size = DEFAULT_INITIAL_CAPACITY;
        }
        SimpleEntry<K, V>[] resizedArray = createArrayOfEntries(size * 2);
        for (int i = 0; i < size; i++) {
            resizedArray[i] = entries[i];
        }
        entries = resizedArray;
    }

    @Override
    public V remove(Object key) {
        V value = get(key);
        if (value == null) {
            return null;
        }
        int i = 0;
        while (!Objects.equals(entries[i].getKey(), key)) {
            i++;
        }
        entries[i] = entries[size - 1];
        entries[size - 1] = null;
        size -= 1;
        return value;
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            entries[i] = null;
        }
        size = 0;
    }

    @Override
    public boolean containsKey(Object key) {
        for (int i = 0; i < size; i++) {
            if (entries[i].getKey().equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        // Note: You may or may not need to change this method, depending on whether you
        // add any parameters to the ArrayMapIterator constructor.
        return new ArrayMapIterator<>(this.entries);
    }

    private static class ArrayMapIterator<K, V> implements Iterator<Map.Entry<K, V>> {
        private final SimpleEntry<K, V>[] entries;
        // You may add more fields and constructor parameters
        int index;

        public ArrayMapIterator(SimpleEntry<K, V>[] entries) {
            this.entries = entries;
            this.index = 0;
        }

        @Override
        public boolean hasNext() {
            if (this.index < this.entries.length && this.entries[index] != null) {
                return true;
            }
            return false;
        }

        @Override
        public Map.Entry<K, V> next() {
            if (this.entries[index] == null) {
                throw new NoSuchElementException();
            }
            SimpleEntry<K, V> thing = this.entries[index];
            index += 1;
            return thing;
        }
    }
}
