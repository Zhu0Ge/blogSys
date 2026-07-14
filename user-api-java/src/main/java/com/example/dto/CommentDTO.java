package com.example.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CommentDTO {
    private Integer id;
    private String content;
    private String username;
    private LocalDateTime createdAt;
    private List<CommentDTO> replies;

    public CommentDTO(Integer id, String content, String username, LocalDateTime createdAt, List<CommentDTO> replies) {
        this.id = id;
        this.content = content;
        this.username = username;
        this.createdAt = createdAt;
        this.replies = replies;
    }

    public Integer getId() { return id; }
    public String getContent() { return content; }
    public String getUsername() { return username; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<CommentDTO> getReplies() { return replies; }
}