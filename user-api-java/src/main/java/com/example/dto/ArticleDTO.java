package com.example.dto;

import com.example.model.Article;
import java.time.LocalDateTime;

public class ArticleDTO {
    private Integer id;
    private String title;
    private String content;
    private String username;
    private LocalDateTime createdAt;

    // JPA 构造
    public ArticleDTO(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.username = article.getUser().getUsername();
        this.createdAt = article.getCreatedAt();
    }

    // JDBC 查询用
    public ArticleDTO() {}

    public Integer getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getUsername() { return username; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Integer id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setUsername(String username) { this.username = username; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}