package com.example.springbootpractice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;       // Spring 自动注入邮件发送器

    @Value("${spring.mail.username}")        // 从配置文件读发件人邮箱
    private String fromEmail;

    /**
     * 异步发送欢迎邮件
     * @Async: 这个方法会在独立线程中执行，不阻塞主流程
     */
    @Async
    public void sendWelcomeEmail(String toEmail, String username) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);           // 发件人
            message.setTo(toEmail);               // 收件人
            message.setSubject("欢迎注册用户管理系统");  // 邮件标题
            message.setText("你好 " + username + "！\n\n"
                    + "欢迎注册用户管理系统！\n"
                    + "你的账号已创建成功。\n\n"
                    + "如有问题请联系管理员。");   // 邮件正文

            mailSender.send(message);             // 发送邮件
            logger.info("[邮件] 欢迎邮件发送成功 → {}", toEmail);

        } catch (Exception e) {
            logger.error("[邮件] 发送失败 → {} | 错误: {}", toEmail, e.getMessage());
            // 注意：这里 catch 住不抛出，避免邮件发送失败影响注册流程
        }
    }
}
