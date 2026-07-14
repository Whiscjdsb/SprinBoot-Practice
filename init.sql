-- Docker MySQL 初始化脚本
-- MySQL 容器首次启动时自动执行

USE test;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(50) NOT NULL,
    `age` INT,
    `password` VARCHAR(100),
    `avatar` VARCHAR(255),
    `role` VARCHAR(20) DEFAULT 'user',
    `email` VARCHAR(100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 任务表
CREATE TABLE IF NOT EXISTS `task` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(200) NOT NULL,
    `description` TEXT,
    `status` INT DEFAULT 0,
    `priority` INT DEFAULT 0,
    `assignee_id` BIGINT,
    `creator_id` BIGINT,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入测试数据
INSERT INTO `user` (`name`, `age`, `password`, `role`, `email`) VALUES
('admin', 25, '$2b$10$ndwDO9KA.m8DPWhL9dJLT.r3SkEa2/yt3zw7us7fDni13CIZYmU0q', 'admin', 'admin@test.com'),
('zhangsan', 20, '$2b$10$ndwDO9KA.m8DPWhL9dJLT.r3SkEa2/yt3zw7us7fDni13CIZYmU0q', 'user', 'zhangsan@test.com');
