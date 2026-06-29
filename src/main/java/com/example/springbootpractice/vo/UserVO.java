package com.example.springbootpractice.vo;

public class UserVO {
    private  Long id;
    private String name;
    private Integer age;

    public UserVO() {
    }
    public UserVO(Long id, String name, Integer age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public static UserVO fromEntity(com.example.springbootpractice.entity.User user) {
        if (user == null) {
            return null;
        }
        return new UserVO(user.getId(), user.getName(), user.getAge());
    }
    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public Integer getAge() {
        return age;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
}
