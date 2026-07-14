package com.example.springbootpractice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springbootpractice.common.ApiResponse;
import com.example.springbootpractice.dto.CreateTaskRequest;
import com.example.springbootpractice.dto.UpdateTaskStatusRequest;
import com.example.springbootpractice.entity.Task;
import com.example.springbootpractice.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    public ApiResponse<Task> createTask(@Valid @RequestBody CreateTaskRequest request) {
        Task task = taskService.createTask(request);
        return ApiResponse.success(task);
    }
    @GetMapping("/{id}")
    public ApiResponse<Task> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        return ApiResponse.success(task);
    }
    @GetMapping
    public ApiResponse<Page<Task>> getAllTasks(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
            Page<Task> tasks = taskService.getAllTasks(page, size);
            return ApiResponse.success(tasks);
    }
    @PutMapping("/{id}")
    public ApiResponse<Task> updateTask(@PathVariable Long id, @Valid @RequestBody CreateTaskRequest request) {
        Task task = taskService.updateTask(id, request);
        return ApiResponse.success(task);
    }
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteTask(@PathVariable Long id) {
         taskService.deleteTask(id);
        return ApiResponse.success("删除成功");
    }
    @PatchMapping("/{id}/status")
    public ApiResponse<Task> updateTaskStatus(@PathVariable Long id, @Valid @RequestBody UpdateTaskStatusRequest request) {
        Task task = taskService.updateTaskStatus(id, request.getStatus());
        return ApiResponse.success(task);
    }
}
