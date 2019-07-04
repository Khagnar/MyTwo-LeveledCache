import java.io.Serializable;

public interface LeveledCache<K, V extends Serializable> extends BaseCache<K, V>, FrequencyCall<K> {
    void recache();
}
