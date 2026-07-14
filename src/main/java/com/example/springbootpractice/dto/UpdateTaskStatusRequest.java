package com.example.springbootpractice.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTaskStatusRequest {
    @NotNull(message = "状态不能为空")
    private Integer status;
}
