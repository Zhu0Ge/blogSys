package com.example.service;

import java.util.List;

import com.example.dto.CommentDTO;
import com.example.common.R;

public interface ICommentService {
    // 发表评论
    R<CommentDTO> createComment(String content, Integer articleId, Integer userId, Integer parentId);

    // 获取某篇文章的所有评论
    R<List<CommentDTO>> getArticleComments(Integer articleId);

    // 删除评论（只允许评论者删除）
    R<Void> deleteComment(Integer id, Integer userId);  
}
