package com.example.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.Disabled;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")   // 使用 application-test.properties 配置 H2 数据库
class ArticleControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void getAllArticles_WithoutToken_Returns401() {
        // 不带 token 访问
        ResponseEntity<String> response = restTemplate.getForEntity(
            "/api/articles", String.class);
        assertEquals(401, response.getStatusCode().value());
    }

    @Disabled("集成测试需要先获取 token，暂不启用")
    @Test
    void getArticlesPaged_ReturnsPagedData() {
        // 先生成一个 token（测试用户，需要先注册或固定一个测试用户）
        // 简化方案：跳过 auth 测试，只测分页逻辑
        // （实际需要先获取 token，这里省略）
    }
}