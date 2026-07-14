package com.example.controller;

import com.example.dto.CreateArticleRequest;
import com.example.model.Article;
import com.example.service.IArticleService;

import jakarta.validation.Valid;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final IArticleService articleService;

    public ArticleController(IArticleService articleService) {
        this.articleService = articleService;
    }

    // 获取当前登录用户的 ID（从 JWT token 解析出来的）
    private Integer getCurrentUserId() {
        return (Integer) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }

    // 获取所有文章
    @GetMapping
    public List<Map<String, Object>> getAllArticles() {
        return articleService.getAllArticles();
    }

    // 搜索文章
    @GetMapping("/search")
    public List<Map<String, Object>> searchArticles(@RequestParam String q) {
        return articleService.searchArticles(q);
    }

    // 获取单篇文章
    @GetMapping("/{id}")
    public Article getArticle(@PathVariable Integer id) {
        return articleService.getArticleById(id);
    }

    // 创建文章
    @PostMapping
    public Map<String, Object> createArticle(@RequestBody @Valid CreateArticleRequest req) {
        Article article = articleService.createArticle(
            req.title(),
            req.content(),
            getCurrentUserId()
        );
        return Map.of("message", "Article created", "articleId", article.getId());
    }

    // 更新文章
    @PutMapping("/{id}")
    public Map<String, String> updateArticle(@PathVariable Integer id, @RequestBody @Valid CreateArticleRequest req) {
        articleService.updateArticle(
            id,
            req.title(),
            req.content(),
            getCurrentUserId()
        );
        return Map.of("message", "Article updated");
    }

    // 删除文章
    @DeleteMapping("/{id}")
    public Map<String, String> deleteArticle(@PathVariable Integer id) {
        Integer userId = getCurrentUserId();
        articleService.deleteArticle(id, userId);
        return Map.of("message", "Article deleted");
    }

    // 获取某个用户的所有文章
    @GetMapping("/user/{userId}")
    public List<Article> getUserArticles(@PathVariable Integer userId) {
        return articleService.getUserArticles(userId);
    }
}