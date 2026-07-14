package com.example.service;

import com.example.model.User;

public interface IUserService {
    User register(String username, String email, String password);
    User loginByEmail(String email, String password);
    User getUserById(Integer id);
    User updateProfile(Integer userId, String avatar, String bio);
}