package by.laba1.demo.api.aspects;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class CounterAspect {

    private final Map<String, AtomicInteger> methodCallCount = new ConcurrentHashMap<>();

    @Pointcut("@annotation(by.laba1.demo.api.aspects.RequestCounter)")
    public void counterMethods() {}

    @Before("counterMethods()")
    public void countMethodCall(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        methodCallCount.putIfAbsent(methodName, new AtomicInteger(0));
        int count = methodCallCount.get(methodName).incrementAndGet();
        log.info("Method '{}' called {} times", methodName, count);
    }

    public void resetStatistics() {
        methodCallCount.clear();
    }

    public Map<String, AtomicInteger> getMethodCallStatistics() {
        return methodCallCount;
    }
}

