package com.example.springbootpractice.task;

import com.example.springbootpractice.entity.User;
import com.example.springbootpractice.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class ScheduledTask {

    private final Logger logger = LoggerFactory.getLogger(ScheduledTask.class);

    @Autowired
    private UserMapper userMapper;

    // ==================== 1. fixedRate ====================
    // 每 10 秒执行一次，不管上次有没有执行完
    @Scheduled(fixedRate = 10000)
    public void heartbeat() {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        logger.info("[心跳] 当前时间: {}", time);
    }

    // ==================== 2. fixedDelay ====================
    // 上次执行结束后，等 5 秒再执行下一次
    @Scheduled(fixedDelay = 5000)
    public void reportUserCount() {
        List<User> users = userMapper.selectList(null);
        logger.info("[统计] 当前用户总数: {}", users.size());
    }

    // ==================== 3. cron ====================
    // 每分钟执行一次（测试用，正式环境可以改成 "0 0 2 * * ?" 每天凌晨2点）
    @Scheduled(cron = "0 * * * * ?")
    public void cleanup() {
        logger.info("[清理] 执行定时清理任务...");
        // 这里可以写清理过期数据、清理临时文件等逻辑
    }
}
