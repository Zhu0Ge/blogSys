package com.example.service;

import com.example.common.R;
import com.example.dto.CommentDTO;
import com.example.model.Comment;
import com.example.model.User;
import com.example.repository.CommentRepository;
import com.example.repository.UserRepository;
import com.example.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    void deleteComment_NotOwner_ThrowsException() {
        // Arrange
        User owner = new User();
        owner.setId(1);

        Comment comment = new Comment();
        comment.setId(1);
        comment.setUser(owner);

        when(commentRepository.findById(1)).thenReturn(Optional.of(comment));

        // Act & Assert：user 2 想删 user 1 的评论
        assertThrows(RuntimeException.class, () -> {
            commentService.deleteComment(1, 2);
        });
    }

    @Test
    void deleteComment_Owner_Success() {
        User owner = new User();
        owner.setId(1);

        Comment comment = new Comment();
        comment.setId(1);
        comment.setUser(owner);

        when(commentRepository.findById(1)).thenReturn(Optional.of(comment));

        R<Void> result = commentService.deleteComment(1, 1);

        assertEquals(200, result.getCode());
        verify(commentRepository).delete(comment);  // 验证 delete 被调用了
    }

    @Test
    void deleteComment_NonExisting_ThrowsException() {
        when(commentRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            commentService.deleteComment(999, 1);
        });
    }
}