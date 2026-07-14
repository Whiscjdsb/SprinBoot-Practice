package com.example.springbootpractice.aspect;

import com.example.springbootpractice.annotation.RateLimit;
import com.example.springbootpractice.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Deque;
import java.util.ArrayDeque;

@Aspect
@Component
public class RateLimitAspect {

    private final Logger logger = LoggerFactory.getLogger(RateLimitAspect.class);

    /**
     * 每个IP一个队列，记录请求时间戳
     * key: IP地址
     * value: 该IP的请求时间戳队列
     */
    private final ConcurrentHashMap<String, Deque<Long>> requestMap = new ConcurrentHashMap<>();

    @Around("@annotation(rateLimit)")
    public Object checkRateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        // 1. 获取客户端 IP
        String ip = getClientIp();
        long now = System.currentTimeMillis();
        int windowMs = rateLimit.window() * 1000;  // 秒转毫秒
        int max = rateLimit.max();

        // 2. 获取该IP的请求队列，没有就新建
        Deque<Long> timestamps = requestMap.computeIfAbsent(ip, k -> new ArrayDeque<>());

        // 3. 加锁，清理过期时间戳（滑动窗口）
        synchronized (timestamps) {
            // 清理：删除窗口之外的时间戳
            while (!timestamps.isEmpty() && now - timestamps.peekFirst() > windowMs) {
                timestamps.pollFirst();
            }

            // 4. 检查是否超过限制
            if (timestamps.size() >= max) {
                logger.warn("[限流] IP:{} 请求过于频繁，{}/{}秒内已请求{}次", ip, windowMs / 1000, max, timestamps.size());
                throw new BusinessException(429, "请求过于频繁，请稍后再试");
            }

            // 5. 没超限，记录这次请求的时间戳
            timestamps.addLast(now);
        }

        logger.info("[限流] IP:{} 当前窗口内第{}/{}次请求", ip, timestamps.size(), max);
        return joinPoint.proceed();
    }

    /**
     * 获取客户端真实IP
     */
    private String getClientIp() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return "UNKNOWN";

        HttpServletRequest request = attributes.getRequest();
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
