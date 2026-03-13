package com.coredesk.repository;

import com.coredesk.enums.TicketStatus;
import com.coredesk.model.Ticket;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long>, JpaSpecificationExecutor<Ticket> {
    long countByStatus(TicketStatus status);
    long countByCreatedBy_EmailAndStatus(String email, TicketStatus status);
    long countByAssignedTo_EmailAndStatus(String email, TicketStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Ticket t WHERE t.id = ?1")
    Optional<Ticket> findForProcess(Long ticketId);
}
