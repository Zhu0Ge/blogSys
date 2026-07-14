package com.example.service.impl;

import com.example.model.Article;
import com.example.dto.ArticleDTO;
import com.example.model.User;
import com.example.repository.ArticleRepository;
import com.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.example.service.IArticleService;
import com.example.common.R;

import java.util.*;


@Service
public class ArticleServiceImpl implements IArticleService {

    private final ArticleRepository articleRepository;

    public ArticleServiceImpl(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;

    }

    // 创建文章
    public R<ArticleDTO> createArticle(String title, String content, Integer userId) {
        Article article = new Article();
        article.setTitle(title);
        article.setContent(content);
        return R.success(new ArticleDTO(articleRepository.save(article)));
    }

    // 获取所有文章（按时间倒序）
    public R<List<ArticleDTO>> getAllArticles() {
        List<Article> articles = articleRepository.findAllByOrderByCreatedAtDesc();
        List<ArticleDTO> result = new ArrayList<>();
        for (Article article : articles) {
            result.add(new ArticleDTO(article));
        }
        return R.success(result);
    }



    // 根据 ID 获取单篇文章
    public R<ArticleDTO> getArticleById(Integer id) {
        Article article = articleRepository.findById(id).orElseThrow(() -> new RuntimeException("Article not found"));
        return R.success(new ArticleDTO(article));
    }

    // 获取某个用户的所有文章
    public R<List<ArticleDTO>> getUserArticles(Integer userId) {
        List<Article> articles = articleRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<ArticleDTO> result = new ArrayList<>();
        for (Article article : articles) {
            result.add(new ArticleDTO(article));
        }
        return R.success(result);
    }

    // 更新文章（只允许作者更新）
    public R<ArticleDTO> updateArticle(Integer id, String title, String content, Integer userId) {
        Article article = articleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Article not found"));
        if (!article.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only edit your own articles");
        }
        article.setTitle(title);
        article.setContent(content);
        return R.success(new ArticleDTO(articleRepository.save(article)));
    }

    // 删除文章（只允许作者删除）
    public R<Void> deleteArticle(Integer id, Integer userId) {
        Article article = articleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Article not found"));
        if (!article.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only delete your own articles");
        }
        articleRepository.delete(article);
        return R.success(null);
    }

    public R<List<ArticleDTO>> searchArticles(String keyword) {
        List<Article> articles = articleRepository
            .findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(keyword);
        
        List<ArticleDTO> result = new ArrayList<>();
        for (Article article : articles) {
            result.add(new ArticleDTO(article));
            
        }
        return R.success(result);
    }
}