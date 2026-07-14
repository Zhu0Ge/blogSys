package com.example.service.impl;

import com.example.model.Comment;
import com.example.dto.CommentDTO;
import com.example.common.R;
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
    public R<CommentDTO> createComment(String content, Integer articleId, Integer userId, Integer parentId) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setArticleId(articleId);
        comment.setParentId(parentId);
        comment.setUserId(userId);
        Comment savedComment = commentRepository.save(comment);
        String username = userRepository.findById(savedComment.getUserId())
                .map(User::getUsername)
                .orElse("Unknown");
        return R.success(new CommentDTO(savedComment.getId(), savedComment.getContent(), 
            username,savedComment.getCreatedAt(),new ArrayList<>()));
    }

    // 获取某篇文章的所有父评论
    public R<List<CommentDTO>> getArticleComments(Integer articleId) {
        List<Comment> comments = commentRepository.findByArticleIdAndParentIdIsNullOrderByCreatedAtAsc(articleId);
        List<CommentDTO> articleComments = new ArrayList<>();
        for (Comment comment : comments) {
            articleComments.add(toCommentDTO(comment));
        }
        return R.success(articleComments);
    }

    private CommentDTO toCommentDTO(Comment comment) {
        //查用户名
        String username = userRepository.findById(comment.getUserId())
                .map(User::getUsername)
                .orElse("Unknown");
        //查这个评论的所有回复
        List<Comment> replies = commentRepository.findByParentIdOrderByCreatedAtAsc(comment.getId());
        List<CommentDTO> replyList = new ArrayList<>();
        for (Comment reply : replies) {
            replyList.add(toCommentDTO(reply));
        }
        return new CommentDTO(comment.getId(), comment.getContent(), 
            username,comment.getCreatedAt(),replyList);
    }

    // 删除评论（只允许评论者删除）
    public R<Void> deleteComment(Integer id, Integer userId) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("You can only delete your own comments");
        }
        commentRepository.delete(comment);
        return R.success(null);
    }
}