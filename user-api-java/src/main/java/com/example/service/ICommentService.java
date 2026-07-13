package com.example.service;

import java.util.List;
import java.util.Map;

import com.example.model.Comment;

public interface ICommentService {
    // 发表评论
    Comment createComment(String content, Integer articleId, Integer userId);

    // 获取某篇文章的所有评论
    List<Map<String, Object>> getArticleComments(Integer articleId);

    // 删除评论（只允许评论者删除）
    void deleteComment(Integer id, Integer userId);
}
