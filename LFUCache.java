import java.util.*;
public class LFUCache<Key, Value> {
    // The LFUCache stores a bounded amount of data evicting the least
    // frequently accessed data values.
    // We achieve this by using the following data structures
    // a. A map for constant access, inserts and updates.
    // b. A reverse map tracking all the keys at a particular frequency.
    //
    // Note:
    // In the following implementation remove() has not been implemented, as
    // that is not typically required in caches.
    //
    // Not implementing remove() makes tracking the lowest frequency simpler as
    // it only needs to be updated on accesses or when new entries are added,
    // in both these cases it is obvious which would be the lowest frequency.
    //
    // If remove() is to be added we need some more elaborate tracking of the
    // lowest frequency using say a linked list.
    static class ValueFrequency<Value> {
        Value value;
        int frequency;
    }
    static int MAX_SIZE = 100;
    Map<Key, ValueFrequency<Value>> map = new HashMap<>();
    Map<Integer, Set<Key>> frequencyMap = new HashMap<>();
    int lowestFrequency;

    Value put(Key key, Value value) {
        // If the map does not contain the key, proceed with new key value
        // insertion flow.
        if (!map.containsKey(key)) {
            insertNewKeyValue(key, value);
            // no previous values.
            return null;
        }

        // If the data item exists, update the frequency and the value to be
        // stored.
        ValueFrequency<Value> valueFrequency = increaseFrequency(key);
        valueFrequency.value = value;
        return map.put(key, valueFrequency).value;
    }

    void insertNewKeyValue(Key key, Value value) {
        if (map.size() >= MAX_SIZE) {
            evictLowestFrequencyKey();
        }

        // Add key to the minimal frequency set and update lowest frequency.
        frequencyMap.putIfAbsent(1, new HashSet<>());
        frequencyMap.get(1).add(key);
        lowestFrequency = 1;

        // Insert the value frequency into the map.
        ValueFrequency<Value> valueFrequency = new ValueFrequency();
        valueFrequency.value = value;
        valueFrequency.frequency = 1;
        map.put(key, valueFrequency);
    }

    Value get(Key key) {
        if (!map.containsKey(key)) {
            return null;
        }

        // If the data item exists, increase the frequency.
        increaseFrequency(key);
        return map.get(key).value;
    }

    // This routine will perform the following
    // a. Increase the frequency tracked within the data item.
    // b. Update the frequency map.
    // c. Remove empty entries from the frequency map.
    // d. Update the lowest frequency entry.
    ValueFrequency increaseFrequency(Key key) {
        // Increase the frequency.
        ValueFrequency valueFrequency = map.get(key);
        int frequency = valueFrequency.frequency;
        valueFrequency.frequency++;

        // Add to the key set of the increased frequency.
        frequencyMap.putIfAbsent(frequency+1, new HashSet<>());
        Set<Key> keysAtNextFrequency = frequencyMap.get(frequency+1);
        keysAtNextFrequency.add(key);

        // Remove from the key set of the current frequency.
        Set<Key> keysAtThisFrequency = frequencyMap.get(frequency);
        keysAtThisFrequency.remove(key);
        // Update the lowest frequency if necessary.
        if (keysAtThisFrequency.isEmpty()) {
            frequencyMap.remove(frequency);
            if (lowestFrequency == frequency) {
                lowestFrequency = frequency + 1;
            }
        }
        return valueFrequency;
    }

    // NOTE::
    // This function could leave the value of lowest frequency inconsistent,
    // so it has to be used in the context of a higher level routine that
    // manages the lowest frequency value correctly.
    void evictLowestFrequencyKey() {
        // Since multiple data items could be at the current lowest frequency,
        // we choose the first one in the iteration order.
        Key lowestFrequencyKey =
                frequencyMap.get(lowestFrequency).iterator().next();
        map.remove(lowestFrequencyKey);
        frequencyMap.get(lowestFrequency).remove(lowestFrequencyKey);
        if (frequencyMap.get(lowestFrequency).isEmpty()) {
            frequencyMap.remove(lowestFrequency);
        }
    }


    void print() {
        for (Map.Entry<Key, ValueFrequency<Value>> entry: map.entrySet()) {
            System.out.println(entry.getKey() + "::" + entry.getValue().value);
        }
    }

    public static void main(String[] args) {
        LFUCache<Integer, String> lfuCache = new LFUCache<>();
        for (int i = 0; i < 1000; i++) {
            lfuCache.put(i, "value" + i);
            // This extra access will prevent keys "0-49" from getting evicted.
            if (i < 50) {
                lfuCache.get(i);
            }
        }
        lfuCache.print();
    }
}