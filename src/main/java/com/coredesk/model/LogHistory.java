package com.coredesk.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "ticket_log_history")
@NoArgsConstructor
@AllArgsConstructor
public class LogHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long ticketId;
    private String createdBy;
    private String status;
    private String description;

    @CreationTimestamp
    private LocalDateTime createdAt;
}