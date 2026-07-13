package com.example.service.impl;

import com.example.model.Comment;
import com.example.model.User;
import com.example.repository.CommentRepository;
import com.example.repository.UserRepository;
import com.example.service.ICommentService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements ICommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public CommentServiceImpl(CommentRepository commentRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    // 发表评论
    public Comment createComment(String content, Integer articleId, Integer userId, Integer parentId) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setArticleId(articleId);
        comment.setParentId(parentId);
        comment.setUserId(userId);
        return commentRepository.save(comment);
    }

    // 获取某篇文章的所有父评论
    public List<Map<String, Object>> getArticleComments(Integer articleId) {
        List<Comment> comments = commentRepository.findByArticleIdAndParentIdIsNullOrderByCreatedAtAsc(articleId);
        List<Map<String, Object>> articleComments = new ArrayList<>();
        for (Comment comment : comments) {
            articleComments.add(commentToMap(comment));
        }
        return articleComments;
    }

    private Map<String, Object> commentToMap(Comment comment) {
        Map<String, Object> item = new HashMap<>();
        item.put("id", comment.getId());
        item.put("content", comment.getContent());
        item.put("articleId", comment.getArticleId());
        item.put("userId", comment.getUserId());
        item.put("createdAt", comment.getCreatedAt());

        //查用户名
        String username = userRepository.findById(comment.getUserId())
                .map(User::getUsername)
                .orElse("Unknown");
        item.put("username", username);

        //查这个评论的所有回复
        List<Comment> replies = commentRepository.findByParentIdOrderByCreatedAtAsc(comment.getId());
        List<Map<String, Object>> replyList = new ArrayList<>();
        for (Comment reply : replies) {
            replyList.add(commentToMap(reply));
        }
        item.put("replies", replyList);
        return item;
    }

    // 删除评论（只允许评论者删除）
    public void deleteComment(Integer id, Integer userId) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("You can only delete your own comments");
        }
        commentRepository.delete(comment);
    }
}