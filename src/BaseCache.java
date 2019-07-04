public interface BaseCache<K, V> {
    /** размер коллекции **/
    int size();

    /** удаление всех элементов из коллекции **/
    void clearCache();

    /** добавление элемента в коллекцию **/
    void cache(K key, V value);

    /** получение элемента коллекции **/
    V getObject(K key);

    /** удаление элемента коллекции **/
    void deleteObject(K key);

    /** проверка на содержание элемента в коллекции **/
    boolean containsKey(K key);

    /** удаление элемента коллекции и получение его значения **/
    V removeObject(K key);
}
