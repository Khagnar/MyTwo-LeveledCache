import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

public class TwoLeveledCache<K, V extends Serializable> implements LeveledCache<K, V> {

    private RamCache<K, V> ramCache;
    private HardDriveCache<K, V> hardDriveCache;
    private int numberOfRequests;
    private int numberOfRequestsForReCache;

    public TwoLeveledCache(int numberOfRequestsForReCache) {
        this.numberOfRequestsForReCache = numberOfRequestsForReCache;
        numberOfRequests = 0;
        ramCache = new RamCache<>();
        hardDriveCache = new HardDriveCache<>();
    }

    @Override
    public int size() {
        return ramCache.size() + hardDriveCache.size();
    }

    @Override
    public void clearCache() {
        ramCache.clearCache();
        hardDriveCache.clearCache();
    }

    @Override
    public void cache(K key, V value) {
        ramCache.cache(key, value);
    }

    @Override
    public V getObject(K key) {
        if (ramCache.containsKey(key)) {
            numberOfRequests++;
            if (numberOfRequests > numberOfRequestsForReCache) {
                this.recache();
                numberOfRequests = 0;
            }
            return ramCache.getObject(key);
        }
        if (hardDriveCache.containsKey(key)) {
            numberOfRequests++;
            if (numberOfRequests > numberOfRequestsForReCache) {
                this.recache();
                numberOfRequests = 0;
            }
            return hardDriveCache.getObject(key);
        }
        return null;
    }

    @Override
    public void deleteObject(K key) {
        if (ramCache.containsKey(key)) ramCache.deleteObject(key);
        if (hardDriveCache.containsKey(key)) hardDriveCache.deleteObject(key);
    }

    @Override
    public boolean containsKey(K key) {
        return ramCache.containsKey(key) || hardDriveCache.containsKey(key);
    }

    @Override
    public V removeObject(K key) {
        if (ramCache.containsKey(key)) return ramCache.removeObject(key);
        if (hardDriveCache.containsKey(key)) return hardDriveCache.removeObject(key);
        return null;
    }

    @Override
    public Set getMostFrequentlyUsedKeys() {
        Set<K> set = new TreeSet<>(ramCache.getMostFrequentlyUsedKeys());
        set.addAll(hardDriveCache.getMostFrequentlyUsedKeys());
        return set;
    }

    @Override
    public int getFrequencyOfCallingObject(K key) {
        if (ramCache.containsKey(key)) return ramCache.getFrequencyOfCallingObject(key);
        if (hardDriveCache.containsKey(key)) return hardDriveCache.getFrequencyOfCallingObject(key);
        return 0;
    }

    @Override
    public void recache() {
        Set<K> ramKeySet = new TreeSet<>(ramCache.getMostFrequentlyUsedKeys());

        int averageFrequency = 0;
        for (K key : ramKeySet) {
            averageFrequency += ramCache.getFrequencyOfCallingObject(key);
        }
        averageFrequency /= ramKeySet.size();

        for (K key : ramKeySet) {
            if (ramCache.getFrequencyOfCallingObject(key) <= averageFrequency) {
                hardDriveCache.cache(key, ramCache.removeObject(key));
            }
        }

        Set<K> setHardDrive = new TreeSet<>(hardDriveCache.getMostFrequentlyUsedKeys());

        for (K key : setHardDrive) {
            if (hardDriveCache.getFrequencyOfCallingObject(key) > averageFrequency) {
                ramCache.cache(key, hardDriveCache.removeObject(key));
            }
        }
    }
}
