package com.example.service.impl;

import com.example.model.Article;
import com.example.dto.ArticleDTO;
import com.example.model.User;
import com.example.repository.ArticleRepository;
import org.springframework.stereotype.Service;
import com.example.service.IArticleService;
import com.example.common.R;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


import java.util.*;
import java.util.stream.Collectors;



@Service
public class ArticleServiceImpl implements IArticleService {

    private final ArticleRepository articleRepository;
    private final org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    public ArticleServiceImpl(ArticleRepository articleRepository, org.springframework.jdbc.core.JdbcTemplate jdbcTemplate) {
        this.articleRepository = articleRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    // 创建文章
    public R<ArticleDTO> createArticle(String title, String content, Integer userId) {
        Article article = new Article();
        article.setTitle(title);
        article.setContent(content);

        User user = new User();
        user.setId(userId);
        article.setUser(user);

        Article saved = articleRepository.save(article);
        Article fresh = articleRepository.findById(saved.getId()).orElseThrow();

        return R.success(new ArticleDTO(fresh));
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

    @Override
    public R<List<ArticleDTO>> searchArticlesFulltext(String keyword) {
        String formatted = Arrays.stream(keyword.split("\\s+"))
            .filter(w -> !w.isBlank())
            .map(w -> "+" + w + "*")
            .collect(Collectors.joining(" "));

        String sql = "SELECT a.id, a.title, a.content, a.created_at, u.username " +
                     "FROM articles a LEFT JOIN users u ON a.user_id = u.id " +
                     "WHERE MATCH(a.title) AGAINST(? IN BOOLEAN MODE) " +
                     "ORDER BY a.created_at DESC LIMIT 50";

        List<ArticleDTO> result = jdbcTemplate.query(sql, new Object[]{formatted}, (rs, rowNum) -> {
            ArticleDTO dto = new ArticleDTO();
            dto.setId(rs.getInt("id"));
            dto.setTitle(rs.getString("title"));
            dto.setContent(rs.getString("content"));
            dto.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            dto.setUsername(rs.getString("username"));
            return dto;
        });

        return R.success(result);
    }

    @Override
    public R<Map<String, Object>> getArticlesPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Article> articlePage = articleRepository.findAllBy(pageable);

        List<ArticleDTO> articleDTOs = articlePage.getContent().stream()
                .map(ArticleDTO::new)
                .toList();

        Map<String, Object> result = new HashMap<>();
        result.put("articles", articleDTOs);
        result.put("totalPages", articlePage.getTotalPages());
        result.put("currentPage", articlePage.getNumber());
        result.put("totalElements", articlePage.getTotalElements());
        result.put("hasNext", articlePage.hasNext());
        result.put("hasPrevious", articlePage.hasPrevious());

        return R.success(result);
    }
}