package com.example.springbootpractice.service;

import com.example.springbootpractice.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TaskNotificationConsumer {

    @RabbitListener(queues = RabbitConfig.TASK_QUEUE)
    public void handleTaskCreated(String message) {
        System.out.println("[消费者] 收到消息 -> " + message);
        System.out.println("[消费者] 模拟发送通知...");
        System.out.println("[消费者] 处理完成！");
    }
}
