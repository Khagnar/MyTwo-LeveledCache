import java.io.*;
import java.util.*;

public class HardDriveCache<K, V extends Serializable> implements BaseCache<K, V>, FrequencyCall<K> {

    private Map<K, String> mapHardDrive;
    private Map<K, Integer> mapFrequency;
    private static final String  PATH = "Path to file" ;

    public HardDriveCache() {
        mapHardDrive = new HashMap<>();
        mapFrequency = new TreeMap<>();
    }

    @Override
    public int size() {
        return mapHardDrive.size();
    }

    @Override
    public void clearCache() {
        for (K key : mapHardDrive.keySet()) {
            File deleteFile = new File(mapHardDrive.get(key));
            deleteFile.delete();
        }
        mapHardDrive.clear();
        mapFrequency.clear();
    }

    @Override
    public void cache(K key, V value) {
        String pathToFile = PATH + UUID.randomUUID().toString();
        mapHardDrive.put(key, pathToFile);
        mapFrequency.put(key, 1);

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(pathToFile);
            ObjectOutputStream oos = new ObjectOutputStream(fileOutputStream);

            oos.writeObject(value);
            oos.flush();
            oos.close();

            fileOutputStream.flush();
            fileOutputStream.close();

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public V getObject(K key) {
        if (containsKey(key)) {
            String pathToFile = mapHardDrive.get(key);

            try {
                FileInputStream fileInputStream = new FileInputStream(pathToFile);
                ObjectInputStream ois = new ObjectInputStream(fileInputStream);

                V value = (V)ois.readObject();

                int freq = mapFrequency.remove(key);
                mapFrequency.put(key, ++freq);

                ois.close();
                fileInputStream.close();

                return value;
            }
            catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Override
    public void deleteObject(K key) {
        if (containsKey(key)) {
            File deleteFile = new File(mapHardDrive.remove(key));
            mapFrequency.remove(key);
            deleteFile.delete();
        }
    }

    @Override
    public boolean containsKey(K key) {
        return mapHardDrive.containsKey(key);
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
