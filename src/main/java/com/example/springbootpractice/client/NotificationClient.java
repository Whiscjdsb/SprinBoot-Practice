package com.example.springbootpractice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "notification-service", fallbackFactory = NotificationClientFallback.class)
public interface NotificationClient {
    @GetMapping("/api/notifications/ping")
    String ping();
}
