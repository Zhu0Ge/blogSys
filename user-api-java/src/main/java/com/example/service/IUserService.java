package com.example.service;

import com.example.dto.UserVO;
import com.example.common.R;

public interface IUserService {
    R<UserVO> register(String username, String email, String password);
    R<UserVO> loginByEmail(String email, String password);
    R<UserVO> getUserById(Integer id);
    R<UserVO> updateProfile(Integer userId, String avatar, String bio);
}