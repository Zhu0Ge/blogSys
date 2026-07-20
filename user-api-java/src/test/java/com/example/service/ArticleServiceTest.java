package com.example.service;

import com.example.common.R;
import com.example.dto.ArticleDTO;
import com.example.model.Article;
import com.example.model.User;
import com.example.repository.ArticleRepository;
import com.example.service.impl.ArticleServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private ArticleServiceImpl articleService;

    @Test
    void getArticleById_ExistingId_ReturnsArticle() {
        // Arrange（准备）
        User user = new User();
        user.setId(1);
        user.setUsername("testuser");

        Article article = new Article();
        article.setId(1);
        article.setTitle("Test Title");
        article.setContent("Test Content");
        article.setUser(user);

        when(articleRepository.findById(1)).thenReturn(Optional.of(article));

        // Act（执行）
        R<ArticleDTO> result = articleService.getArticleById(1);

        // Assert（验证）
        assertEquals(200, result.getCode());
        assertEquals("Test Title", result.getData().getTitle());
        assertEquals("testuser", result.getData().getUsername());
    }

    @Test
    void getArticleById_NonExistingId_ThrowsException() {
        // Arrange
        when(articleRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            articleService.getArticleById(999);
        });
    }

    @Test
    void deleteArticle_NotOwner_ThrowsException() {
        // Arrange：文章属于 user 1，但请求用 user 2
        User articleOwner = new User();
        articleOwner.setId(1);

        Article article = new Article();
        article.setId(1);
        article.setUser(articleOwner);

        when(articleRepository.findById(1)).thenReturn(Optional.of(article));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            articleService.deleteArticle(1, 2);  // user 2 想删 user 1 的文章
        });
    }
}