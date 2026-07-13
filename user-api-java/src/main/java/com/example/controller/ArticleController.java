package com.example.controller;

import com.example.model.Article;
import com.example.service.IArticleService;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final IArticleService articleService;

    public ArticleController(IArticleService articleService) {
        this.articleService = articleService;
    }

    // 获取所有文章
    @GetMapping
    public List<Map<String, Object>> getAllArticles() {
        return articleService.getAllArticles();
    }

    // 获取单篇文章
    @GetMapping("/{id}")
    public Article getArticle(@PathVariable Integer id) {
        return articleService.getArticleById(id);
    }

    // 创建文章
    @PostMapping
    public Map<String, Object> createArticle(@RequestBody Map<String, String> body) {
        Article article = articleService.createArticle(
            body.get("title"),
            body.get("content"),
            Integer.valueOf(body.get("userId"))
        );
        return Map.of("message", "Article created", "articleId", article.getId());
    }

    // 更新文章
    @PutMapping("/{id}")
    public Map<String, String> updateArticle(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        articleService.updateArticle(
            id,
            body.get("title"),
            body.get("content"),
            Integer.valueOf(body.get("userId"))
        );
        return Map.of("message", "Article updated");
    }

    // 删除文章
    @DeleteMapping("/{id}")
    public Map<String, String> deleteArticle(@PathVariable Integer id, @RequestParam Integer userId) {
        articleService.deleteArticle(id, userId);
        return Map.of("message", "Article deleted");
    }

    // 获取某个用户的所有文章
    @GetMapping("/user/{userId}")
    public List<Article> getUserArticles(@PathVariable Integer userId) {
        return articleService.getUserArticles(userId);
    }
}