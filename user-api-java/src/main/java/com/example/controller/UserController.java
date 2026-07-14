package com.example.controller;

import com.example.common.R;
import com.example.dto.UserVO;
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
    public R<UserVO> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String email = body.get("email");
        String password = body.get("password");

        return userService.register(username, email, password);
    }

    // POST /api/login
    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        R<UserVO> userVO = userService.loginByEmail(email, password);

        // 生成 JWT token
        String token = jwtUtil.generateToken(userVO.getData().getId(), userVO.getData().getUsername());

        return R.success(Map.of("token", token, "user", userVO.getData()));
    }

    // GET /api/users/:userId
    @GetMapping("/users/{userId}")
    public R<UserVO> getUserById(@PathVariable Integer userId) {
        return userService.getUserById(userId);
    }

    // PUT /api/profile — 更新个人资料
    @PutMapping("/profile")
    public R<String> updateProfile(@RequestBody Map<String, String> body) {
        Integer userId = getCurrentUserId();
        String avatar = body.get("avatar");
        String bio = body.get("bio");
        userService.updateProfile(userId, avatar, bio);
        return R.success("Profile updated");
    }

    // GET /api/profile — 获取个人资料
    @GetMapping("/profile")
    public R<UserVO> getProfile() {
        return userService.getUserById(getCurrentUserId());
    
    }
}