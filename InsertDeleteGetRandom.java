import java.util.*;
public class InsertDeleteGetRandom {
    static class IDGR<Key, Value> {
        static class ValueLocation<Value> {
            Value value;
            Integer location;
        }
        Random random = new Random();
        // Use the map for constant lookup and removal.
        Map<Key, ValueLocation<Value>> map = new HashMap<>();
        // Use list for a sense of ordering and random access.
        List<Key> list = new ArrayList<>();
        void insert(Key key, Value value) {
            // If you find the key, then just update the value and return.
            ValueLocation<Value> valueLocation = map.get(key);
            if (valueLocation != null) {
                valueLocation.value = value;
                return;
            }
            // If this is a new key value.
            // Add key to the list so it acquires a position.
            // Add key, value + location to the map.
            list.add(key);
            ValueLocation<Value> newValueLocation = new ValueLocation<>();
            newValueLocation.value = value;
            newValueLocation.location = list.size() - 1;
            map.put(key, newValueLocation);
        }

        void delete(Key key) {
            // If you dont find the key, return early.
            ValueLocation<Value> valueLocation = map.get(key);
            if (valueLocation == null) {
                return;
            }
            // If key is in the map.
            // Remove key, value from map.
            // Remove key from the list and adjust list.
            Integer location = valueLocation.location;
            Key lastKey = list.get(list.size() - 1);
            list.set(location, lastKey);
            map.get(lastKey).location = location;
            list.remove(list.size() - 1);
            map.remove(key);
        }

        Value getRandom() {
            // Find a random index uptil the size of the map.
            // Find the Key stored in the list at this index.
            // Return the value of this random key.
            Key randomKey = list.get(random.nextInt(map.size()));
            return map.get(randomKey).value;
        }
    }

    public static void main(String[] args) {
        IDGR<Integer, String> table = new IDGR<>();

        for (int i = 0; i < 100; i++) {
            table.insert(i, "value" + i);
        }

        for (int i = 0; i < 50; i++) {
            System.out.println(table.getRandom());
        }

        for (int i = 0; i < 100; i++) {
            if (i % 2 == 1) {
                table.delete(i);
            }
        }

        for (int i = 0; i < 50; i++) {
            System.out.println(table.getRandom());
        }
    }
}
