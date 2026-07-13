package com.example.controller;

import com.example.model.Comment;
import com.example.service.ICommentService;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final ICommentService commentService;

    public CommentController(ICommentService commentService) {
        this.commentService = commentService;
    }

    // 获取某篇文章的评论
    @GetMapping("/article/{articleId}")
    public List<Map<String, Object>> getArticleComments(@PathVariable Integer articleId) {
        return commentService.getArticleComments(articleId);
    }

    // 发表评论
    @PostMapping
    public Map<String, Object> createComment(@RequestBody Map<String, String> body) {
        Comment comment = commentService.createComment(
            body.get("content"),
            Integer.valueOf(body.get("articleId")),
            Integer.valueOf(body.get("userId"))
        );
        return Map.of("message", "Comment created", "commentId", comment.getId());
    }

    // 删除评论
    @DeleteMapping("/{id}")
    public Map<String, String> deleteComment(@PathVariable Integer id, @RequestParam Integer userId) {
        commentService.deleteComment(id, userId);
        return Map.of("message", "Comment deleted");
    }
}