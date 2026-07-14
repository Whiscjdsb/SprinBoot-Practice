package com.example.springbootpractice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // 队列名称
    public static final String TASK_QUEUE = "task.notification.queue";
    // 交换机名称
    public static final String TASK_EXCHANGE = "task.notification.exchange";
    // 路由键
    public static final String TASK_ROUTING_KEY = "task.create";

    // 1. 创建队列
    @Bean
    public Queue taskNotificationQueue() {
        return new Queue(TASK_QUEUE, true);
    }

    // 2. 创建交换机
    @Bean
    public TopicExchange taskNotificationExchange() {
        return new TopicExchange(TASK_EXCHANGE);
    }

    // 3. 绑定队列到交换机
    @Bean
    public Binding bindingTaskNotification() {
        return BindingBuilder
                .bind(taskNotificationQueue())
                .to(taskNotificationExchange())
                .with(TASK_ROUTING_KEY);
    }
}
