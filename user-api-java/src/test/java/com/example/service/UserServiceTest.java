package com.example.service;

import com.example.common.R;
import com.example.dto.UserVO;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.service.impl.UserServiceImpl;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void register_NewUsername_Success() {
        // Arrange
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(1);
            return saved;
        });

        // Act
        R<UserVO> result = userService.register("newuser", "new@test.com", "password123");

        // Assert
        assertEquals(200, result.getCode());
        assertEquals("newuser", result.getData().getUsername());
        assertEquals("new@test.com", result.getData().getEmail());
    }

    @Test
    void register_ExistingUsername_ThrowsException() {
        // Arrange
        when(userRepository.findByUsername("existing")).thenReturn(Optional.of(new User()));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userService.register("existing", "a@b.com", "password");
        });
    }

    @Disabled("需要处理 BCryptPasswordEncoder 的 Mock，暂不启用")
    @Test
    void loginByEmail_CorrectPassword_Success() {
        
    }

    @Test
    void loginByEmail_WrongEmail_ThrowsException() {
        when(userRepository.findByEmail("not@exist.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            userService.loginByEmail("not@exist.com", "password");
        });
    }

    @Test
    void getUserById_ExistingId_Success() {
        User user = new User();
        user.setId(1);
        user.setUsername("testuser");
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        R<UserVO> result = userService.getUserById(1);

        assertEquals(200, result.getCode());
        assertEquals("testuser", result.getData().getUsername());
    }

    @Test
    void getUserById_NonExistingId_ThrowsException() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            userService.getUserById(999);
        });
    }
}