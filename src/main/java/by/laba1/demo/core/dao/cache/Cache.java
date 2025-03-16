package by.laba1.demo.core.dao.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Cache <K, V> {
    private final ConcurrentHashMap<K, V> cache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<K, Long> timestamps = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private final long maxAgeInMillis;

    public Cache(long maxAgeInMillis) {
        this.maxAgeInMillis = maxAgeInMillis;
        startCleanupTask();
    }

    public void put(K key, V value) {
        cache.put(key, value);
        timestamps.put(key, System.currentTimeMillis());
    }

    public V get(K key) {
        Long addedTime = timestamps.get(key);
        if (addedTime == null || (System.currentTimeMillis() - addedTime > maxAgeInMillis)) {
            remove(key);
            return null;
        }
        return cache.get(key);
    }

    public void remove(K key) {
        cache.remove(key);
        timestamps.remove(key);
    }

    public void clear() {
        cache.clear();
    }

    public int size() {
        return cache.size();
    }

    private void startCleanupTask() {
        executor.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            for (Map.Entry<K, Long> entry : timestamps.entrySet()) {
                if (now - entry.getValue() > maxAgeInMillis) {
                    remove(entry.getKey());
                }
            }
        }, maxAgeInMillis, maxAgeInMillis, TimeUnit.MILLISECONDS);
    }
}
