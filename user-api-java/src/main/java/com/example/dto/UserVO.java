package com.example.dto;

import com.example.model.User;
import java.time.LocalDateTime;

// 返回给前端的用户信息（不包含 password）
public class UserVO {
    private Integer id;
    private String username;
    private String email;
    private String avatar;
    private String bio;
    private LocalDateTime createdAt;

    public UserVO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.avatar = user.getAvatar();
        this.bio = user.getBio();
        this.createdAt = user.getCreatedAt();
    }

    public Integer getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getAvatar() { return avatar; }
    public String getBio() { return bio; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}