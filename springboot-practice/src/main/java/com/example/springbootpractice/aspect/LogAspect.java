package com.example.springbootpractice.aspect;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
@Component

public class LogAspect {
    private final Logger logger = LoggerFactory.getLogger(LogAspect.class);


    @Pointcut("execution(* com.example.springbootpractice.controller..*.*(..))")
    public void controllerPointcut() {
    }

    @Around("controllerPointcut()")
    public Object aroundController(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        Object result = joinPoint.proceed();

        long costTime = System.currentTimeMillis() - startTime;

        String resultJson = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(result);
        logger.info("调用方法: {} {} 参数 {} 耗时 {} 结果: {}", className, methodName, args, costTime, resultJson);
        return result;
    }
}

