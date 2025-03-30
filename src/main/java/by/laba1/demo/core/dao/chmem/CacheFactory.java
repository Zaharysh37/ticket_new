package by.laba1.demo.core.dao.chmem;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CacheFactory {
    private final ConfigurableApplicationContext context;

    public <K, V> MyCache<K, V> createCache(String cacheName, int maxSize, long timeout) {
        return context.getBean(MyCache.class, cacheName, maxSize, timeout);
    }
}