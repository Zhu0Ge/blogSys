package com.example.controller;

import com.example.model.User;
import com.example.service.IUserService;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
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

        return Map.of(
            "message", "Login successful",
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
}