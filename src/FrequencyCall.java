import java.util.Set;

public interface FrequencyCall<K> {
    Set<K> getMostFrequentlyUsedKeys();
    int getFrequencyOfCallingObject(K key);
}
