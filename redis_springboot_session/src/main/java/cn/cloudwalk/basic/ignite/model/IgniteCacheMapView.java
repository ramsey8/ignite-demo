package cn.cloudwalk.basic.ignite.model;

import org.apache.ignite.IgniteCache;
import org.jetbrains.annotations.NotNull;
import org.springframework.session.SessionRepository;

import javax.cache.Cache;
import java.util.*;

/**
 * 替换 {@link SessionRepository} 存储，内部使用 {@link IgniteCache} 缓存
 * 在高并发环境：{@link IgniteCacheMapView} 虽然直接实现 {@link Map}，
 * 但是 {@link IgniteCache} 的单步操作都是原子安全
 * @param <K>
 * @param <V>
 */
public class IgniteCacheMapView<K, V> implements Map<K, V> {

    private final IgniteCache<K, V> cache;

    public IgniteCacheMapView(IgniteCache<K, V> cache) {
        this.cache = cache;
    }

    public int size() {
        return cache.size();
    }

    public boolean isEmpty() {
        return cache.size() == 0;
    }

    @SuppressWarnings("unchecked")
    public boolean containsKey(Object key) {
        return cache.containsKey((K) key);
    }

    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    public V get(Object key) {
        return cache.get((K) key);
    }

    @SuppressWarnings("unchecked")
    public V put(Object key, Object value) {
        return cache.getAndPut((K) key, (V) value);
    }

    @SuppressWarnings("unchecked")
    public V remove(Object key) {
        return cache.getAndRemove((K) key);
    }

    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        cache.putAll(m);
    }

    public void clear() {
        cache.clear();
    }

    public Set<K> keySet() {
        Set<K> set = new HashSet<K>();
        synchronized (this){
            Iterator<Cache.Entry<K, V>> iterator = cache.iterator();
            while (iterator.hasNext()) set.add(iterator.next().getKey());
        }
        return set;
    }

    public Collection<V> values() {
        Collection<V> collection = new ArrayList<V>();
        synchronized (this){
            Iterator<Cache.Entry<K, V>> iterator = cache.iterator();
            while (iterator.hasNext()) collection.add(iterator.next().getValue());
        }
        return collection;
    }

    /**
     * 该方法不提供实现
     * @return
     */
    public Set<Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException();
    }
}