package com.coredesk.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentResponse {
    private Long id;
    private String message;
    private UserInfo user;
    private LocalDateTime createdAt;
}
