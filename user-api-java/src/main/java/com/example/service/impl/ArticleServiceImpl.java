package com.example.service.impl;

import com.example.model.Article;
import com.example.model.User;
import com.example.repository.ArticleRepository;
import com.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.example.service.IArticleService;

import java.util.*;


@Service
public class ArticleServiceImpl implements IArticleService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    public ArticleServiceImpl(ArticleRepository articleRepository, UserRepository userRepository) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
    }

    // 创建文章
    public Article createArticle(String title, String content, Integer userId) {
        Article article = new Article();
        article.setTitle(title);
        article.setContent(content);
        article.setUserId(userId);
        return articleRepository.save(article);
    }

    // 获取所有文章（按时间倒序）
    public List<Map<String, Object>> getAllArticles() {
    List<Article> articles = articleRepository.findAllByOrderByCreatedAtDesc();
    List<Map<String, Object>> result = new ArrayList<>();
    
    for (Article article : articles) {
        Map<String, Object> item = new HashMap<>();
        item.put("id", article.getId());
        item.put("title", article.getTitle());
        item.put("content", article.getContent());
        item.put("userId", article.getUserId());
        item.put("createdAt", article.getCreatedAt());
        
        // 查用户名
        String username = userRepository.findById(article.getUserId())
                .map(User::getUsername)
                .orElse("Unknown");
        item.put("username", username);
        
        result.add(item);
    }
    return result;
}

    // 根据 ID 获取单篇文章
    public Article getArticleById(Integer id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));
    }

    // 获取某个用户的所有文章
    public List<Article> getUserArticles(Integer userId) {
        return articleRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // 更新文章（只允许作者更新）
    public Article updateArticle(Integer id, String title, String content, Integer userId) {
        Article article = getArticleById(id);
        if (!article.getUserId().equals(userId)) {
            throw new RuntimeException("You can only edit your own articles");
        }
        article.setTitle(title);
        article.setContent(content);
        return articleRepository.save(article);
    }

    // 删除文章（只允许作者删除）
    public void deleteArticle(Integer id, Integer userId) {
        Article article = getArticleById(id);
        if (!article.getUserId().equals(userId)) {
            throw new RuntimeException("You can only delete your own articles");
        }
        articleRepository.delete(article);
    }

    public List<Map<String, Object>> searchArticles(String keyword) {
        List<Article> articles = articleRepository
            .findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(keyword);
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Article article : articles) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", article.getId());
            item.put("title", article.getTitle());
            item.put("content", article.getContent());
            item.put("userId", article.getUserId());
            item.put("createdAt", article.getCreatedAt());
            
            String username = userRepository.findById(article.getUserId())
                    .map(User::getUsername)
                    .orElse("Unknown");
            item.put("username", username);
            result.add(item);
        }
        return result;
    }
}