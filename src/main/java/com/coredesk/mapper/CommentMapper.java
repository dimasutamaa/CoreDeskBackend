package com.coredesk.mapper;

import com.coredesk.dto.CommentResponse;
import com.coredesk.model.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    private final UserMapper userMapper;

    public CommentResponse toDto(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .message(comment.getMessage())
                .user(userMapper.toDto(comment.getUser()))
                .createdAt(comment.getCreatedAt())
                .build();
    }

}
