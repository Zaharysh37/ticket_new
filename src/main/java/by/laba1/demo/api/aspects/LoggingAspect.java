package by.laba1.demo.api.aspects;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* by.laba1.demo.core.service..*.*(..))")
    public void serviceMethods() {}

    @Pointcut("@annotation(by.laba1.demo.api.aspects.LogExecution)")
    public void logAnnotatedMethods() {}

    @Around("serviceMethods() || logAnnotatedMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        log.info(">> Method: {} | Args: {}", methodName, Arrays.toString(args));

        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - start;
            log.info("<< Method: {} | Execution time: {} ms | Result: {}", methodName, duration, result);
            return result;
        } catch (Exception e) {
            log.error("!! Exception in {}: {}", methodName, e.getMessage());
            throw e;
        }
    }
}



