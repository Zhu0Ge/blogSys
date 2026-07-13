package com.example.repository;

import com.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    // 按用户名查找
    Optional<User> findByUsername(String username);

    // 按邮箱查找
    Optional<User> findByEmail(String email);
}