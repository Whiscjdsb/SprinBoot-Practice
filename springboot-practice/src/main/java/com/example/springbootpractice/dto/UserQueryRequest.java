package com.example.springbootpractice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class UserQueryRequest {
    @Min(value = 1, message = "页码不能小于1")
    private Integer page = 1;
    @Min(value = 1, message = "每页数量不能小于1")
    @Max(value = 100, message = "每页数量不能大于100")
    private Integer size = 10;
    private String name;
    @Min(value = 0, message = "年龄不能小于0")
    private Integer age;

    public Integer getPage() {
        return page;
    }
    public Integer getSize() {
        return size;
    }
    public String getName() {
        return name;
    }
    public Integer getAge() {
        return age;
    }
    public void setPage(Integer page) {
        this.page = page;
    }
    public void setSize(Integer size) {
        this.size = size;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
}
