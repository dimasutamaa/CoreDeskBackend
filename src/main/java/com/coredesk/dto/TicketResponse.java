package com.coredesk.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TicketResponse {
    private Long id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private LocalDateTime slaDate;
    private UserInfo createdBy;
    private UserInfo processedBy;
    private UserInfo assignedTo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
