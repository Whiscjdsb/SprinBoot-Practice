package com.example.springbootpractice.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("task")
public class Task implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String description;
    private Integer status;
    private Integer priority;
    private Long assigneeId;
    private Long creatorId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
