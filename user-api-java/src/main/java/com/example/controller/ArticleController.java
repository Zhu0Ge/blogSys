package com.example.controller;

import com.example.common.R;
import com.example.dto.CreateArticleRequest;
import com.example.dto.ArticleDTO;
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
    public R<List<ArticleDTO>> getAllArticles() {
        return articleService.getAllArticles();  // getAllArticles 返回 List<ArticleDTO>
    }

    // 搜索文章
    @GetMapping("/search")
    public R<List<ArticleDTO>> searchArticles(@RequestParam String q) {
        return articleService.searchArticles(q);
    }

    // 获取单篇文章
    @GetMapping("/{id}")
    public R<ArticleDTO> getArticle(@PathVariable Integer id) {
        return articleService.getArticleById(id);  // 返回 ArticleDTO
    }

    // 创建文章
    @PostMapping
    public R<ArticleDTO> createArticle(@RequestBody @Valid CreateArticleRequest req) {
        return articleService.createArticle(req.title(), req.content(), getCurrentUserId());
    }

    // 更新文章
    @PutMapping("/{id}")
    public R<String> updateArticle(@PathVariable Integer id, @RequestBody @Valid CreateArticleRequest req) {
        articleService.updateArticle(
            id,
            req.title(),
            req.content(),
            getCurrentUserId()
        );
        return R.success("Article updated");
    }

    // 删除文章
    @DeleteMapping("/{id}")
    public R<String> deleteArticle(@PathVariable Integer id) {
        articleService.deleteArticle(id, getCurrentUserId());
        return R.success("Article deleted");
    }

    // 获取某个用户的所有文章
    @GetMapping("/user/{userId}")
    public R<List<ArticleDTO>> getUserArticles(@PathVariable Integer userId) {
        return articleService.getUserArticles(userId);
    }
}