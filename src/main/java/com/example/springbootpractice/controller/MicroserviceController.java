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
        // 第一步：调用 notificationClient.ping()，保存到 result
        String result = notificationClient.ping();
        // 第二步：把 result 包装成成功响应并返回
        return ApiResponse.success(result);
    }
}