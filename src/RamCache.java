import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class RamCache<K, V> implements BaseCache<K, V>, FrequencyCall<K> {

    private Map<K, V> mapRam;
    private Map<K, Integer> mapFrequency;

    public RamCache() {
        mapRam = new HashMap<>();
        mapFrequency = new TreeMap<>();
    }

    @Override
    public int size() {
        return mapRam.size();
    }

    @Override
    public void clearCache() {
        mapRam.clear();
        mapFrequency.clear();
    }

    @Override
    public void cache(K key, V value) {
        mapRam.put(key, value);
        mapFrequency.put(key, 1);
    }

    @Override
    public V getObject(K key) {
        if (containsKey(key)) {
            int freq = mapFrequency.get(key);
            mapFrequency.put(key, ++freq);
            return mapRam.get(key);
        }
        return null;
    }

    @Override
    public void deleteObject(K key) {
        if (containsKey(key)) {
            mapRam.remove(key);
            mapFrequency.remove(key);
        }
    }

    @Override
    public boolean containsKey(K key) {
        return mapRam.containsKey(key);
    }

    @Override
    public V removeObject(K key) {
        if (containsKey(key)) {
            V value = this.getObject(key);
            this.deleteObject(key);
            return value;
        }
        return null;
    }

    @Override
    public Set<K> getMostFrequentlyUsedKeys() {
        MyComparator comp = new MyComparator(mapFrequency);
        Map<K, Integer> sorted = new TreeMap<>(comp);
        sorted.putAll(mapFrequency);
        return sorted.keySet();
    }

    @Override
    public int getFrequencyOfCallingObject(K key) {
        if (containsKey(key)) return mapFrequency.get(key);
        return 0;
    }
}
