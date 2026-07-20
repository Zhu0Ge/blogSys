package com.example.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // 注入测试用的 secret（因为 JwtUtil 用了 @Value("${jwt.secret}")）
        ReflectionTestUtils.setField(jwtUtil, "secretString", 
            "test-secret-key-for-testing-only-32-characters!!");
        jwtUtil.init();
    }

    @Test
    void generateToken_ValidInput_ReturnsToken() {
        String token = jwtUtil.generateToken(1, "testuser");
        assertNotNull(token);
        assertTrue(token.startsWith("eyJ"));  // JWT 的 Header 部分
    }

    @Test
    void extractUserId_ValidToken_ReturnsCorrectId() {
        String token = jwtUtil.generateToken(42, "testuser");
        Integer userId = jwtUtil.extractUserId(token);
        assertEquals(42, userId);
    }

    @Test
    void validateToken_InvalidToken_ReturnsFalse() {
        assertFalse(jwtUtil.validateToken("invalid.token.here"));
    }

    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        String token = jwtUtil.generateToken(1, "testuser");
        assertTrue(jwtUtil.validateToken(token));
    }
}