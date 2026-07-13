package com.example.repository;

import com.example.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    // 按文章 ID 查所有评论
    List<Comment> findByArticleIdOrderByCreatedAtAsc(Integer articleId);
}