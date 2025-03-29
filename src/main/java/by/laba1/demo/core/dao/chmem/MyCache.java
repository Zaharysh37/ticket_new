package by.laba1.demo.core.dao.chmem;

import by.laba1.demo.api.aspects.LogExecution;
import by.laba1.demo.api.aspects.RequestCounter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import java.util.function.Supplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyCache<K, V> {
    private final int maxSize;
    private final long defaultTimeout;
    private final Map<K, CacheEntry<V>> cache;
    private final ScheduledExecutorService scheduler;

    @Getter
    @Setter
    @RequiredArgsConstructor
    private static class CacheEntry<V> {
        private final V value;
        private final long expiryTime;
    }

    public MyCache(int maxSize, long defaultTimeout) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("Cache size must be greater than 0");
        }
        if (defaultTimeout < 10_000) {
            throw new IllegalArgumentException("Timeout must be at least 10ms");
        }

        this.maxSize = maxSize;
        this.defaultTimeout = defaultTimeout;
        this.cache = Collections.synchronizedMap(new LinkedHashMap<>(this.maxSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, CacheEntry<V>> eldest) {
                return size() > maxSize;
            }
        });

        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        startCleanupTask();
    }

    private void startCleanupTask() {
        scheduler.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            cache.entrySet().removeIf(entry -> entry.getValue().expiryTime < now);
        }, defaultTimeout / 6, defaultTimeout / 6, TimeUnit.MILLISECONDS);
    }

    public void put(K key, V value) {
        put(key, value, defaultTimeout);
    }

    @LogExecution
    @RequestCounter
    public void put(K key, V value, long timeout) {
        long expiryTime = System.currentTimeMillis() + timeout;
        cache.put(key, new CacheEntry<>(value, expiryTime));
    }

    @LogExecution
    @RequestCounter
    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry != null && entry.expiryTime >= System.currentTimeMillis()) {
            log.info("Cache hit for key: {}", key);
            return entry.value;
        }
        cache.remove(key);
        return null;
    }

    @LogExecution
    @RequestCounter
    public V get(K key, Supplier<V> valueLoader) {
        V value = this.get(key);

        if (value != null) {
            log.info("Cache hit for key: {}", key);
            return value;
        }

        value = valueLoader.get();

        this.put(key, value);
        return value;
    }

    @LogExecution
    public void remove(K key) {
        cache.remove(key);
    }

    @LogExecution
    public void clear() {
        cache.clear();
    }

    public int size() {
        return cache.size();
    }
}