package com.example.springbootpractice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    // LogInterceptor 已移除，日志功能由 LogAspect 统一处理
}
