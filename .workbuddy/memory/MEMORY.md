## Spring Boot 练习项目

### 项目路径
C:\Users\Whiscjdsb\Documents\springboot-practice

### 技术栈
Spring Boot 3.2.5 + Java 17 + MySQL(test库/user表) + Redis + MyBatis-Plus 3.5.5

### 用户偏好
- 教学模式：逐步引导，区分"自己写"和"复制粘贴"
- 每次修改指明具体文件和行号
- 只用 Postman 测试，不用 Swagger UI 测试
- 不帮改文件，除非用户说"你做"
- 通用配置代码（Security、JwtUtil 等）可复制粘贴
- 业务逻辑代码（Controller、Service）让用户自己写

### Phase 完成情况
- P1-3: CRUD + 参数校验 + 异常处理 + 分页
- P4: Spring Security + JWT
- P5: Swagger
- P6: Redis 缓存 (@Cacheable)
- P7: 文件上传 (MultipartFile)
- P8: 角色权限 (AOP + @RequiresPermission)
- P9: 日志系统 (LogAspect + logback + GlobalExceptionHandler)
- P10: 定时任务 (@Scheduled)
- P11: 全局限流 (@RateLimit + 滑动窗口)
- P12: 异步任务 + 邮件发送 (@Async + JavaMailSender)
- P13: Excel 导入导出 (EasyExcel)
- P15: 单元测试 (JUnit 5 + Mockito)
- 综合练习: EasyExcel批量导入+异步邮件+日志
- 任务管理系统(TODO): CRUD+状态流转+权限控制(SecurityUtils)+分页+Redis缓存 ✅
- TaskService 单元测试: 5个测试方法全部通过 (getTaskById×2 + updateTaskStatus×3) ✅
- Docker 部署: Dockerfile多阶段构建 + docker-compose三服务编排 + healthcheck + init.sql自动建表 ✅

### 关键 bug 记录
- JwtUtil 中 role 硬编码为 "user" → 改成变量
- UserVO 未实现 Serializable → Redis 序列化失败
- 文件上传用相对路径 → 改绝对路径
- SecurityConfig 未放行 /uploads/** → 403
- Task 实体未实现 Serializable → Redis 缓存序列化失败
- 缺少 RedisConfig → "Cannot find cache named 'task'" 错误
- @Autowired 只作用于紧接的下一行，多个字段需各自加 @Autowired
- QueryWrapper 字段名要和数据库列名一致（user 表是 name 不是 username）
- Docker: application.properties 优先级高于 application.yml，两个同时存在时 .properties 覆盖 .yml
- Docker: depends_on 只保证启动顺序，需 healthcheck + condition: service_healthy 保证服务就绪
- Docker: MySQL 容器 /docker-entrypoint-initdb.d/ 下的 .sql 仅在首次初始化时执行（需 down -v 删卷重建）
