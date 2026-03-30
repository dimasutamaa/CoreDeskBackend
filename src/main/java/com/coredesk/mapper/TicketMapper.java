package com.coredesk.mapper;

import com.coredesk.dto.TicketResponse;
import com.coredesk.model.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TicketMapper {

    private final UserMapper userMapper;

    public TicketResponse toDto(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .status(ticket.getStatus().name())
                .priority(ticket.getPriority().name())
                .slaDate(ticket.getSlaDate())
                .createdBy(userMapper.toDto(ticket.getCreatedBy()))
                .processedBy(userMapper.toDto(ticket.getProcessedBy()))
                .assignedTo(userMapper.toDto(ticket.getAssignedTo()))
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .build();
    }

}
