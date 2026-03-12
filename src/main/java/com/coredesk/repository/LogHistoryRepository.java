package com.coredesk.repository;

import com.coredesk.model.LogHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogHistoryRepository extends JpaRepository<LogHistory, Long> {
    List<LogHistory> findByTicketIdOrderByCreatedAtDesc(Long ticketId);
}
