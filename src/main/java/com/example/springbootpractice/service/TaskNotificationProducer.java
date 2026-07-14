package com.example.springbootpractice.service;

import com.example.springbootpractice.config.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class TaskNotificationProducer {

    private final RabbitTemplate rabbitTemplate;

    public TaskNotificationProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendTaskCreatedMessage(String taskTitle) {
        String message = "新任务已创建：" + taskTitle;
        rabbitTemplate.convertAndSend(
                RabbitConfig.TASK_EXCHANGE,
                RabbitConfig.TASK_ROUTING_KEY,
                message
        );
        System.out.println("[生产者] 消息已发送 -> " + message);
    }
}
