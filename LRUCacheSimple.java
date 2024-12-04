import java.util.*;

public class LRUCacheSimple {
    // Typically we would need the following to implement an LRU
    // a. A Map for constant retrieval.
    // b. A Linked List of Keys for tracking the age.
    // The linked list avoids the complexity of managing timestamp order.
    // Java provides a LinkedHashMap class that provides the same functionality.
    // It allows size bounding through an overridable method
    // removeEldestEntry().
    // It adjusts the map entry positions in the list both on accesses or
    // inserts.
    static class LRU<Key, Value> {
        int maxEntries;
        LRU(int maxEntries) {
            this.maxEntries = maxEntries;
        }
        LinkedHashMap<Key, Value> cache = new LinkedHashMap<>(/*initialSize
        =*/100, /*loadFactor=*/1, /*accessOrder=*/true) {
            protected boolean removeEldestEntry(Map.Entry entry) {
                return size() > maxEntries;
            }
        };
        Value get(Key key) {
            return cache.get(key);
        }
        void put(Key key, Value value) {
            cache.put(key, value);
        }

        void print() {
            for (Map.Entry entry: cache.entrySet()) {
                System.out.println(entry.getKey() + "::" + entry.getValue());
            }
        }
    }

    public static void main(String[] args) {
        LRU<Integer, String> lruCache = new LRU(100);
        for (int i = 0; i < 2000; i++) {
            lruCache.put(i, "value" + i);
            lruCache.get(i-50);
        }
        lruCache.print();
    }
}
