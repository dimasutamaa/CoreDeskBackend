package com.coredesk.repository;

import com.coredesk.enums.TicketStatus;
import com.coredesk.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long>, JpaSpecificationExecutor<Ticket> {
    long countByCreatedBy_IdAndStatus(Long id, TicketStatus status);
    long countByStatus(TicketStatus status);
}
