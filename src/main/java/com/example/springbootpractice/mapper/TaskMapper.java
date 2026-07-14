package com.example.springbootpractice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springbootpractice.entity.Task;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TaskMapper extends BaseMapper<Task> {
}
