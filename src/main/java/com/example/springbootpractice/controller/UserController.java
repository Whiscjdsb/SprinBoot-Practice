package com.example.springbootpractice.controller;


import com.example.springbootpractice.annotation.RequiresPermission;
import com.example.springbootpractice.common.ApiResponse;
import com.example.springbootpractice.common.PageResult;
import com.example.springbootpractice.dto.CreateUserRequest;
import com.example.springbootpractice.dto.UpdateUserRequest;
import com.example.springbootpractice.dto.UserQueryRequest;
import com.example.springbootpractice.entity.User;
import com.example.springbootpractice.exception.BusinessException;
import com.example.springbootpractice.service.UserService;
import com.example.springbootpractice.vo.UserVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ApiResponse<List<UserVO>> getUsers() {
        return ApiResponse.success(userService.getUsers().stream().map(UserVO::fromEntity).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ApiResponse<UserVO> getUserById(@PathVariable Long id) {
        return ApiResponse.success(userService.getUserById(id));
    }

    @PostMapping
    public ApiResponse<UserVO> addUser(@Valid @RequestBody CreateUserRequest request) {
        return ApiResponse.success(userService.addUser(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<UserVO> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        return ApiResponse.success(userService.updateUser(id, request));
    }

    @RequiresPermission("user:delete")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.success(null);
    }
    @GetMapping("/older-than")
    public ApiResponse<List<UserVO>> getUsersOlderThan(@RequestParam Integer age) {
        return ApiResponse.success(userService.getUsersOlderThan(age).stream().map(UserVO::fromEntity).collect(Collectors.toList()));
    }
    @GetMapping("/search-name")
    public ApiResponse<List<UserVO>> searchUserByName(@RequestParam String name) {
        return ApiResponse.success(userService.searchUserByName(name).stream().map(UserVO::fromEntity).collect(Collectors.toList()));
         }
    @GetMapping("/search")
    public ApiResponse<List<UserVO>> searchUsers(@RequestParam(required = false) String name, @RequestParam(required = false) Integer age) {
        return ApiResponse.success(userService.searchUsers(name, age).stream().map(UserVO::fromEntity).collect(Collectors.toList()));
         }
    @GetMapping("/page")
    public ApiResponse<PageResult<UserVO>> pageUsers(@Valid UserQueryRequest request) {
        return ApiResponse.success(userService.pageUsers(request));
         }
}





