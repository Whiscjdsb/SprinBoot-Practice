package com.example.springbootpractice.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component

public class PerformanceAspect {
    private  static Logger logger = LoggerFactory.getLogger(PerformanceAspect.class);

   @Pointcut("execution(* com.example.springbootpractice.service.*.*(..))")
    public void servicePointcut() {
    }

    @Around("servicePointcut()")
     public Object monitor(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        logger.info("Method {} took {} ms", joinPoint.getSignature().toShortString(), endTime - startTime);
        return result;
    }
}

