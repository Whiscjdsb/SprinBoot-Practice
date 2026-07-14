package com.example.springbootpractice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.springbootpractice.dto.CreateTaskRequest;
import com.example.springbootpractice.entity.Task;
import com.example.springbootpractice.entity.User;
import com.example.springbootpractice.exception.BusinessException;
import com.example.springbootpractice.mapper.TaskMapper;
import com.example.springbootpractice.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private TaskService taskService;

    private Task testTask;

    @BeforeEach
    void setUp() {
        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("测试任务");
        testTask.setDescription("测试描述");
        testTask.setStatus(0);
        testTask.setPriority(1);
        testTask.setCreatorId(1L);
    }

    @Test
    @DisplayName("根据ID查询任务 - 任务存在")
    void getTaskById_Success() {
        when(taskMapper.selectById(1L)).thenReturn(testTask);

        Task result = taskService.getTaskById(1L);

        assertNotNull(result);
        assertEquals("测试任务", result.getTitle());
    }

    @Test
    @DisplayName("根据ID查询任务 - 任务不存在")
    void getTaskById_NotFound() {
        when(taskMapper.selectById(999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> {
            taskService.getTaskById(999L);
        });
    }

    @Test
    @DisplayName("修改状态 - 正常流转 0→1")
    void updateTaskStatus_Success() {
        when(taskMapper.selectById(1L)).thenReturn(testTask);

        Task result = taskService.updateTaskStatus(1L, 1);

        assertEquals(1, result.getStatus());
    }

    @Test
    @DisplayName("修改状态 - 非法流转 0→2")
    void updateTaskStatus_InvalidTransition() {
        when(taskMapper.selectById(1L)).thenReturn(testTask);

        assertThrows(BusinessException.class, () -> {
            taskService.updateTaskStatus(1L, 2);
        });
    }

    @Test
    @DisplayName("修改状态 - 任务不存在")
    void updateTaskStatus_TaskNotFound() {
        when(taskMapper.selectById(999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> {
            taskService.updateTaskStatus(999L, 1);
        });
    }
}
