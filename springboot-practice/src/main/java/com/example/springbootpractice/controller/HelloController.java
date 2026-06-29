package com.example.springbootpractice.controller;
import com.example.springbootpractice.entity.User;
import com.example.springbootpractice.service.UserService;
import org.apache.catalina.connector.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api")
public class HelloController {
    private final UserService userService;
    public HelloController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Spring Boot Hello ";
    }

    @GetMapping("/test")
    public String test() {
        return "test success";
    }





    @GetMapping("/search")
    public String search(@RequestParam String keyword) {
        return "你搜索的是: " + keyword;
    }

    @GetMapping("/profile")
    public User getProfile(@RequestParam String name, @RequestParam int age) {
        return new User(100L, name, age);
    }


}

