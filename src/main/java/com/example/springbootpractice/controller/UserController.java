package com.example.springbootpractice.controller;


import com.example.springbootpractice.annotation.RateLimit;
import com.example.springbootpractice.annotation.RequiresPermission;
import com.example.springbootpractice.common.ApiResponse;
import com.example.springbootpractice.common.PageResult;
import com.example.springbootpractice.dto.ChangePasswordRequest;
import com.example.springbootpractice.dto.CreateUserRequest;
import com.example.springbootpractice.dto.UpdateUserRequest;
import com.example.springbootpractice.dto.UserQueryRequest;
import com.example.springbootpractice.entity.User;
import com.example.springbootpractice.exception.BusinessException;
import com.example.springbootpractice.service.EmailService;
import com.example.springbootpractice.service.ExcelService;
import com.example.springbootpractice.service.UserService;
import com.example.springbootpractice.vo.UserVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.example.springbootpractice.dto.UserExcelDTO;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final EmailService emailService;
    private final ExcelService excelService;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Autowired
    private com.example.springbootpractice.mapper.UserMapper userMapper;

    public UserController(UserService userService, EmailService emailService, ExcelService excelService) {
        this.userService = userService;
        this.emailService = emailService;
        this.excelService = excelService;
    }

    @GetMapping
    public ApiResponse<List<UserVO>> getUsers() {
        return ApiResponse.success(userService.getUsers().stream().map(UserVO::fromEntity).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @RateLimit(window = 60, max = 5)
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

    @PostMapping("/{id}/avatar")
    public ApiResponse<String> uploadAvatar(@PathVariable Long id,
                                            @RequestParam("file") MultipartFile file) throws Exception {
        UserVO user = userService.getUserById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        String originalName = file.getOriginalFilename();
        String suffix = "";
        if (originalName != null && originalName.contains(".")) {
            suffix = originalName.substring(originalName.lastIndexOf("."));
        }
        String fileName = id + suffix;

        // 保存到硬盘
        java.io.File uploadDir = new java.io.File("C:/Users/Whiscjdsb/Documents/springboot-practice/uploads");
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        file.transferTo(new java.io.File(uploadDir, fileName));


        // 存数据库
        String avatarPath = "/uploads/" + fileName;
        userService.updateAvatar(id, avatarPath);

        return ApiResponse.success(avatarPath);
    }
    @PostMapping("/{id}/change-password")
    public ApiResponse<Void> changePassword(@PathVariable Long id, @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(id, request);
        return ApiResponse.success(null);
    }

    @GetMapping("/export")
    public void exportUsers(HttpServletResponse response) throws IOException {
        excelService.exportUsers(response);
    }

    @PostMapping("/import")
    public ApiResponse<String> importUsers(@RequestParam("file") MultipartFile file) throws IOException {

        List<UserExcelDTO> list = new ArrayList<>();

        EasyExcel.read(file.getInputStream(), UserExcelDTO.class, new ReadListener<UserExcelDTO>() {
            @Override
            public void invoke(UserExcelDTO data, AnalysisContext context) {
                list.add(data);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {

            }
        }).sheet().doRead();
        int successCount = 0;
        int failCount = 0;
        for (UserExcelDTO dto : list) {
            try {
                User user = new User();
                user.setName(dto.getName());
                user.setAge(dto.getAge());
                user.setEmail(dto.getEmail());
                user.setRole(dto.getRole() != null ? dto.getRole() : "user");
                user.setPassword(passwordEncoder.encode("123456"));
                userMapper.insert(user);

                emailService.sendWelcomeEmail(dto.getEmail(), dto.getName());
                successCount++;
            } catch (Exception e) {
                failCount++;
            }
        }
        return ApiResponse.success("导入完成！成功" + successCount + "条数据，失败" + failCount + "条数据");
    }

}



