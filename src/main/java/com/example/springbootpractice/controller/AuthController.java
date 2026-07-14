package com.example.springbootpractice.controller;

import com.example.springbootpractice.common.ApiResponse;
import com.example.springbootpractice.dto.CreateUserRequest;
import com.example.springbootpractice.dto.LoginRequest;
import com.example.springbootpractice.entity.User;
import com.example.springbootpractice.security.JwtUtil;
import com.example.springbootpractice.service.EmailService;
import com.example.springbootpractice.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @PostMapping("/login")
    public ApiResponse<String> login(@RequestBody LoginRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        User user = userService.findByUsername(username);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return ApiResponse.error(401, "Invalid username or password");
        }
        String token = JwtUtil.generateToken(username, user.getRole());
        return ApiResponse.success(token);
    }
    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody CreateUserRequest request) {
        userService.addUser(request);
        String token = JwtUtil.generateToken(request.getName(), "user");
        emailService.sendWelcomeEmail(request.getEmail(), request.getName());
        return ApiResponse.success(token);
    }

}
