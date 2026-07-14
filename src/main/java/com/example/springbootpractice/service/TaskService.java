package com.example.springbootpractice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springbootpractice.dto.CreateTaskRequest;
import com.example.springbootpractice.entity.Task;
import com.example.springbootpractice.entity.User;
import com.example.springbootpractice.exception.BusinessException;
import com.example.springbootpractice.mapper.TaskMapper;
import com.example.springbootpractice.mapper.UserMapper;
import com.example.springbootpractice.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;



@Service
public class TaskService {

    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TaskNotificationProducer taskNotificationProducer;


    public Task createTask(CreateTaskRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority() == null ? 1 : request.getPriority());
        task.setAssigneeId(request.getAssigneeId());
        String username = SecurityUtils.getCurrentUsername();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", username);
        User currentUser = userMapper.selectOne(queryWrapper);
        task.setCreatorId(currentUser.getId());
        task.setStatus(0);
        taskMapper.insert(task);
        // TODO: 在这里用 Producer 发送消息通知
        taskNotificationProducer.sendTaskCreatedMessage(task.getTitle());


        return task;
    }

    @Cacheable(value = "task", key = "#id")
    public Task getTaskById(Long id) {
        Task task = taskMapper.selectById(id);
        if (task == null) {
            throw new BusinessException(404, "任务不存在");
        }
        return task;
    }

    @CacheEvict(value = "task", key = "#id")
    public Task updateTask(Long id, CreateTaskRequest request) {
        Task task = taskMapper.selectById(id);
        if (task == null) {
            throw new BusinessException(404, "任务不存在");
        }
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority() == null ? 1 : request.getPriority());
        task.setAssigneeId(request.getAssigneeId());
        taskMapper.updateById(task);
        return task;
    }

    @CacheEvict(value = "task", key = "#id")
    public void deleteTask(Long id) {
        Task task = taskMapper.selectById(id);
        if (task == null) {
            throw new BusinessException(404, "任务不存在");
        }
        // 权限判断：管理员 或 任务创建者本人 才能删
        boolean isAdmin = SecurityUtils.isAdmin();
        String username = SecurityUtils.getCurrentUsername();

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("name", username);
        User currentUser = userMapper.selectOne(wrapper);

        boolean isCreator = task.getCreatorId().equals(currentUser.getId());

        if (!isAdmin && !isCreator) {
            throw new BusinessException(403, "无权删除此任务");
        }

        taskMapper.deleteById(id);
    }

    public Page<Task> getAllTasks(int page, int size) {
        Page<Task> pageParams = new Page<>( page, size);
        return taskMapper.selectPage(pageParams, null);
    }

    // 修改任务状态
    @CacheEvict(value = "task", key = "#id")
    public Task updateTaskStatus(Long id, Integer newStatus) {
        // 1. 先查任务存不存在（跟 getTaskById 一样的套路）
        Task task = taskMapper.selectById(id);
        if (task == null) {
            throw new BusinessException(404, "任务不存在");
        }

        // 2. 拿到当前状态：task.getStatus()
        Integer currentStatus = task.getStatus();


        // 3. 判断能不能改
        boolean canChange = (currentStatus == 0 && newStatus == 1)
                || (currentStatus == 1 && newStatus == 2);

        if (!canChange) {
            throw new BusinessException(400, "状态流转不合法");
        }

// 4. 能改就改
        task.setStatus(newStatus);
        taskMapper.updateById(task);
        return task;
    }

}
