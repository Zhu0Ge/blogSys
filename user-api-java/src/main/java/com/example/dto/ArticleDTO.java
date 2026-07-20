package com.example.dto;

import com.example.model.Article;
import java.time.LocalDateTime;

public class ArticleDTO {
    private Integer id;
    private String title;
    private String content;
    private String username;
    private LocalDateTime createdAt;

    public ArticleDTO(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.username = article.getUser().getUsername();
        this.createdAt = article.getCreatedAt();
    }

    public Integer getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getUsername() { return username; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}