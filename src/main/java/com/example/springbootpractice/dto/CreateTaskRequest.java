package com.example.springbootpractice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateTaskRequest {
    @NotBlank(message = "任务标题不能为空")
    private String title;

    private String description;

    private Integer priority;

    private Long assigneeId;
}
