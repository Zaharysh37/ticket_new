package by.laba1.demo.core.interceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class VisitCounterService {
    private final Map<String, Integer> visitCounts = new ConcurrentHashMap<>();

    public void incrementVisitCount(String url) {
        visitCounts.compute(url, (key, value) -> value == null ? 1 : value + 1);
    }

    public int getVisitCount(String url) {
        return visitCounts.getOrDefault(url, 0);
    }

    public Map<String, Integer> getAllVisitCounts() {
        return new ConcurrentHashMap<>(visitCounts);
    }
}