package com.example.springbootpractice.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class LogAspect {
    private final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Around("execution(* com.example.springbootpractice.controller..*.*(..))")
    public Object aroundController(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 1. 获取 HTTP 请求信息
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        String httpMethod = request != null ? request.getMethod() : "UNKNOWN";
        String requestUrl = request != null ? request.getRequestURI() : "UNKNOWN";
        String ip = getClientIp(request);

        // 2. 获取当前登录用户名
        String username = getCurrentUsername();

        // 3. 获取方法信息
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        // 4. 记录请求开始
        logger.info(">>> 请求开始 | {} {} | 用户:{} | IP:{} | 方法:{}.{}",
                httpMethod, requestUrl, username, ip, className, methodName);

        Object result;
        try {
            // 5. 执行目标方法
            result = joinPoint.proceed();
        } catch (Throwable e) {
            // 6. 如果出错了，记录错误日志
            long costTime = System.currentTimeMillis() - startTime;
            logger.error("<<< 请求异常 | {} {} | 用户:{} | 耗时:{}ms | 错误:{}",
                    httpMethod, requestUrl, username, costTime, e.getMessage());
            throw e;  // 异常继续往上抛，让 GlobalExceptionHandler 处理
        }

        // 7. 正常返回，记录成功日志
        long costTime = System.currentTimeMillis() - startTime;
        logger.info("<<< 请求完成 | {} {} | 用户:{} | 耗时:{}ms",
                httpMethod, requestUrl, username, costTime);

        return result;
    }

    /**
     * 获取客户端真实 IP
     */
    private String getClientIp(HttpServletRequest request) {
        if (request == null) return "UNKNOWN";
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 获取当前登录用户名
     */
    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return "未登录";
        }
        return auth.getName();
    }
}
