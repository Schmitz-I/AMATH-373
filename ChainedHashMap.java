package maps;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @see AbstractIterableMap
 * @see Map
 */
public class ChainedHashMap<K, V> extends AbstractIterableMap<K, V> {
    private static final double DEFAULT_RESIZING_LOAD_FACTOR_THRESHOLD = 1;
    private static final int DEFAULT_INITIAL_CHAIN_COUNT = 1;
    private static final int DEFAULT_INITIAL_CHAIN_CAPACITY = 5;

    /*
    Warning:
    You may not rename this field or change its type.
    We will be inspecting it in our secret tests.
     */
    AbstractIterableMap<K, V>[] chains;
    private int chainCount;
    private double resizeThreshold;
    private int chainCap;
    private int keyValuePairs;
    // You're encouraged to add extra fields (and helper methods) though!

    /**
     * Constructs a new ChainedHashMap with default resizing load factor threshold,
     * default initial chain count, and default initial chain capacity.
     */
    public ChainedHashMap() {
        this(DEFAULT_RESIZING_LOAD_FACTOR_THRESHOLD, DEFAULT_INITIAL_CHAIN_COUNT, DEFAULT_INITIAL_CHAIN_CAPACITY);
    }

    /**
     * Constructs a new ChainedHashMap with the given parameters.
     *
     * @param resizingLoadFactorThreshold the load factor threshold for resizing. When the load factor
     *                                    exceeds this value, the hash table resizes. Must be > 0.
     * @param initialChainCount the initial number of chains for your hash table. Must be > 0.
     * @param chainInitialCapacity the initial capacity of each ArrayMap chain created by the map.
     *                             Must be > 0.
     */
    public ChainedHashMap(double resizingLoadFactorThreshold, int initialChainCount, int chainInitialCapacity) {
        this.chains = this.createArrayOfChains(initialChainCount);
        this.chainCount = initialChainCount;
        this.resizeThreshold = resizingLoadFactorThreshold;
        this.chainCap = chainInitialCapacity;
        this.keyValuePairs = 0;
    }

    /**
     * This method will return a new, empty array of the given size that can contain
     * {@code AbstractIterableMap<K, V>} objects.
     *
     * Note that each element in the array will initially be null.
     *
     * Note: You do not need to modify this method.
     * @see ArrayMap createArrayOfEntries method for more background on why we need this method
     */
    @SuppressWarnings("unchecked")
    private AbstractIterableMap<K, V>[] createArrayOfChains(int arraySize) {
        return (AbstractIterableMap<K, V>[]) new AbstractIterableMap[arraySize];
    }

    /**
     * Returns a new chain.
     *
     * This method will be overridden by the grader so that your ChainedHashMap implementation
     * is graded using our solution ArrayMaps.
     *
     * Note: You do not need to modify this method.
     */
    protected AbstractIterableMap<K, V> createChain(int initialSize) {
        return new ArrayMap<>(initialSize);
    }

    @Override
    public V get(Object key) {
        int index = getIndex(key);
        if (this.chains[index] != null) {
            return this.chains[index].get(key);
        }
        return null;
    }

    private int getIndex(Object key) {
        int hashCode;
        if (key == null) {
            hashCode = 0;
        } else {
            hashCode = Math.abs(key.hashCode());
        }
        int index = hashCode % chainCount;
        return index;
    }

    @Override
    public V put(K key, V value) {
        V oldValue = null;
        int index = getIndex(key);
        // If this index has nothing in it, create new arraymap
        if (this.chains[index] != null) {
            oldValue = chains[index].get(key);
        } else {
            this.chains[index] = createChain(chainCap);
        }
        int sizeCheck = this.chains[index].size();
        // put key and value
        oldValue = this.chains[index].put(key, value);
        // Check if
        if (this.chains[index].size() > sizeCheck) {
            this.keyValuePairs++;
        }
        // Check if threshold is big enough
        double thresholdCheck = (1.0 * keyValuePairs + 1) / chainCount;
        if (thresholdCheck > resizeThreshold) {
            resize();
        }
        return oldValue;
    }

    private void resize() {
        int newCapacity = 2 * chainCount;
        AbstractIterableMap<K, V>[] resizedArray = createArrayOfChains(newCapacity);
        for (Entry<K, V> entry : this) {
            int bucketIndex = Math.abs(entry.getKey().hashCode()) % newCapacity;
            if (resizedArray[bucketIndex] == null) {
                resizedArray[bucketIndex] = createChain(chainCount);
            }
            resizedArray[bucketIndex].put(entry.getKey(), entry.getValue());
        }
        this.chainCount += chainCount;
        this.chains = resizedArray;
    }

    @Override
    public V remove(Object key) {
        int bucketIndex = getIndex(key);
        AbstractIterableMap<K, V> chain = chains[bucketIndex];
        if (key == null || chain == null) {
            return null;
        }
        V removedValue = chain.remove(key);
        if (removedValue != null) {
            keyValuePairs--;
            if (chain.isEmpty()) {
                chains[bucketIndex] = null;
            }
        }
        return removedValue;
    }

    @Override
    public void clear() {
        AbstractIterableMap<K, V>[] newChain = createArrayOfChains(chainCount);
        this.chains = newChain;
        this.keyValuePairs = 0;
    }

    @Override
    public boolean containsKey(Object key) {
        int index = getIndex(key);
        if (this.chains[index] != null) {
            return this.chains[index].containsKey(key);
        }
        return false;
    }

    @Override
    public int size() {
        return this.keyValuePairs;
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        // Note: you won't need to change this method (unless you add more constructor parameters)
        return new ChainedHashMapIterator<>(this.chains);
    }

    /*
    See the assignment webpage for tips and restrictions on implementing this iterator.
     */
    private static class ChainedHashMapIterator<K, V> implements Iterator<Map.Entry<K, V>> {
        private AbstractIterableMap<K, V>[] chains;
        private int index;
        private Iterator<Map.Entry<K, V>> currentIterator;

        public ChainedHashMapIterator(AbstractIterableMap<K, V>[] chains) {
            this.chains = chains;
            index = 0;
            findNextIterator();
        }

        private void findNextIterator() {
            while (index < chains.length && (chains[index] == null || !chains[index].iterator().hasNext())) {
                index++;
            }
            if (index < chains.length) {
                currentIterator = chains[index].iterator();
            }
        }

        @Override
        public boolean hasNext() {
            if (currentIterator != null && currentIterator.hasNext()) {
                return true;
            }
            index++;
            findNextIterator();
            return currentIterator != null && currentIterator.hasNext();
        }

        @Override
        public Map.Entry<K, V> next() {
            if (currentIterator == null || !currentIterator.hasNext()) {
                throw new NoSuchElementException();
            }
            return currentIterator.next();
        }
    }
}

