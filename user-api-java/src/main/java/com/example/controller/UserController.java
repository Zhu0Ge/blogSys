package com.example.controller;

import com.example.model.User;
import com.example.service.IUserService;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.example.util.JwtUtil;
import java.util.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private final IUserService userService;
    private final JwtUtil jwtUtil;

    public UserController(IUserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    private Integer getCurrentUserId() {
        return (Integer) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }

    // POST /api/register
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String email = body.get("email");
        String password = body.get("password");

        User user = userService.register(username, email, password);

        return Map.of("message", "Registration successful", "userId", user.getId());
    }

    // POST /api/login
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        User user = userService.loginByEmail(email, password);

        // 生成 JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        return Map.of(
            "message", "Login successful",
            "token", token,
            "user", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "createdAt", user.getCreatedAt()
            )
        );
    }

    // GET /api/users/:userId
    @GetMapping("/users/{userId}")
    public Map<String, Object> getUserById(@PathVariable Integer userId) {
        User user = userService.getUserById(userId);

        // 1. 先检查 user 是否存在
        if (user == null) {
            return Map.of("code", 404, "msg", "用户不存在");
        }

        // 2. 使用 HashMap 构建返回结果，它允许 value 为 null（虽然最好也不为 null）
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", user); // 这里放入 user 对象
        return result;
    }

    // PUT /api/profile — 更新个人资料
    @PutMapping("/profile")
    public Map<String, Object> updateProfile(@RequestBody Map<String, String> body) {
        Integer userId = getCurrentUserId();
        String avatar = body.get("avatar");
        String bio = body.get("bio");
        User user = userService.updateProfile(userId, avatar, bio);
        return Map.of("message", "Profile updated");
    }

    // GET /api/profile — 获取个人资料
    @GetMapping("/profile")
    public Map<String, Object> getProfile() {
        Integer userId = getCurrentUserId();
        User user = userService.getUserById(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("email", user.getEmail());
        result.put("avatar", user.getAvatar());
        result.put("bio", user.getBio());
        result.put("createdAt", user.getCreatedAt());
        return result;
    }
}