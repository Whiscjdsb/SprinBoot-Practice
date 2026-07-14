package com.example.notificationservice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
@RequestMapping("/api/notifications")


public class NotificationController {

    @Value("${notification.message:notification-service is running}")
    private String message;

    @Value("${server.port}")
    private String port;

    @GetMapping( "/ping")
    public String ping() {
        return message + ",port=" + port;

    }
}
