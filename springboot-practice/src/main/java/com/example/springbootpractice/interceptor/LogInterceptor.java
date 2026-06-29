package com.example.springbootpractice.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class LogInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LogInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 请求到达 Controller 之前
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);
        log.info("[Interceptor] 请求: {} {}", request.getMethod(), request.getRequestURI());
        return true; // true = 放行，false = 拦截
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // Controller 执行完，视图渲染前
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求完全结束后
        long startTime = (long) request.getAttribute("startTime");
        long cost = System.currentTimeMillis() - startTime;
        log.info("[Interceptor] 结束: {} {} | 耗时 {}ms", request.getMethod(), request.getRequestURI(), cost);
    }
}
