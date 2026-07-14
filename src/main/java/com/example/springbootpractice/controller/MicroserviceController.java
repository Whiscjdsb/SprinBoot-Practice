package com.example.springbootpractice.controller;

import com.example.springbootpractice.client.NotificationClient;
import com.example.springbootpractice.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/microservices")
public class MicroserviceController {

    private final NotificationClient notificationClient;

    public MicroserviceController(NotificationClient notificationClient) {
        this.notificationClient = notificationClient;
    }

    // 业务方法写在这里
    @GetMapping("/notification/ping")
    public ApiResponse<String> pingNotification() {
        try {
            String result = notificationClient.ping();
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.success("通知服务暂时不可用，请稍后再试");
        }
    }
}