package by.laba1.demo.core.dao.chmem;

import by.laba1.demo.api.aspects.LogExecution;
import by.laba1.demo.api.aspects.RequestCounter;
import org.springframework.stereotype.Component;

@Component
public class CacheOperations {
    @LogExecution
    @RequestCounter
    public <K, V> void put(MyCache<K, V> cache, K key, V value) {
        cache.put(key, value);
    }
}
