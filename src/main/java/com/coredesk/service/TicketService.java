package com.coredesk.service;

import com.coredesk.dto.FilterCriteria;
import com.coredesk.dto.TicketRequest;
import com.coredesk.enums.Priority;
import com.coredesk.enums.TicketStatus;
import com.coredesk.exception.AppException;
import com.coredesk.model.LogHistory;
import com.coredesk.model.Ticket;
import com.coredesk.model.User;
import com.coredesk.repository.LogHistoryRepository;
import com.coredesk.repository.TicketRepository;
import com.coredesk.repository.UserRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final LogHistoryRepository logHistoryRepository;

    @Transactional
    public Ticket createTicket(TicketRequest request) {
        User user = userRepository.findById(request.getCreatedBy())
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        Ticket ticket = new Ticket();
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setStatus(request.getStatus());
        ticket.setPriority(request.getPriority());
        ticket.setCreatedBy(user);
        ticket.setAssignedTo(null);

        try {
            Ticket savedTicket = ticketRepository.save(ticket);
            createLogHistory(savedTicket.getId(), user.getDisplayName(), savedTicket.getStatus(), "Ticket created");
            return savedTicket;
        } catch (Exception e) {
            log.error("Failed to create ticket: {}", e.getMessage(), e);
            throw e;
        }
    }

    public List<Ticket> getUserTickets(String email, FilterCriteria filter) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        Specification<Ticket> ticketQuery = buildTicketQuery(user, filter);

        return ticketRepository.findAll(ticketQuery);
    }

    public Map<String, Object> getTicketDetail(Long ticketId) {
        Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);
        Map<String, Object> data = new HashMap<>();

        if (ticketOpt.isPresent()) {
            Ticket ticket = ticketOpt.get();
            List<LogHistory> logHistories = logHistoryRepository.findByTicketIdOrderByCreatedAtDesc(ticketId);

            data.put("ticket", ticket);
            data.put("logHistories", logHistories);
        }

        return data;
    }

    public boolean checkUser(String email, Long ticketId, String role) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new AppException("Ticket not found", HttpStatus.NOT_FOUND));

        if ("ADMIN".equals(role) && ticketRepository.existsByProcessedBy_EmailAndAssignedToNull(email)) {
            return ticket.getProcessedBy() != null && ticket.getProcessedBy().getEmail().equals(email);
        }

        return true;
    }

    private Specification<Ticket> buildTicketQuery(User user, FilterCriteria filter) {
        return (ticketRoot, query, cb) -> {
            List<Predicate> conditions = new ArrayList<>();

            applyRoleFilter(user, ticketRoot, cb, conditions);
            applyFilterCriteria(filter, ticketRoot, cb, conditions);

            query.orderBy(cb.desc(ticketRoot.get("createdAt")));
            return cb.and(conditions.toArray(new Predicate[0]));
        };
    }

    private void applyRoleFilter(User user, Root<Ticket> ticketRoot, CriteriaBuilder cb, List<Predicate> conditions) {
        String role = user.getRole();
        if ("USER".equals(role)) {
            conditions.add(cb.equal(ticketRoot.get("createdBy").get("id"), user.getId()));
        }
        if ("AGENT".equals(role)) {
            conditions.add(cb.equal(ticketRoot.get("assignedTo").get("id"), user.getId()));
        }
    }

    private void applyFilterCriteria(FilterCriteria filter, Root<Ticket> ticketRoot, CriteriaBuilder cb, List<Predicate> conditions) {
        if (filter == null) return;

        if (filter.getTitle() != null) {
            String title = "%" + filter.getTitle().toLowerCase() + "%";
            conditions.add(cb.like(cb.lower(ticketRoot.get("title")), title));
        }
        if (filter.getStatus() != null) {
            TicketStatus status = TicketStatus.valueOf(filter.getStatus());
            conditions.add(cb.equal(ticketRoot.get("status"), status));
        }
        if (filter.getPriority() != null) {
            Priority priority = Priority.valueOf(filter.getPriority());
            conditions.add(cb.equal(ticketRoot.get("priority"), priority));
        }
        if (filter.getAssignedTo() != null) {
            conditions.add(cb.equal(ticketRoot.get("assignedTo").get("id"), filter.getAssignedTo()));
        }
        if (filter.getCreatedBy() != null) {
            conditions.add(cb.equal(ticketRoot.get("createdBy").get("id"), filter.getCreatedBy()));
        }
        if (filter.getFrom() != null && filter.getTo() != null) {
            conditions.add(cb.greaterThanOrEqualTo(ticketRoot.get("createdAt"), filter.getFrom().atStartOfDay()));
            conditions.add(cb.lessThanOrEqualTo(ticketRoot.get("createdAt"), filter.getTo().atTime(23, 59, 59)));
        }
    }

    private void createLogHistory(Long ticketId, String username, TicketStatus status, String description) {
        LogHistory log = new LogHistory();
        log.setTicketId(ticketId);
        log.setCreatedBy(username);
        log.setStatus(status.name());
        log.setDescription(description);

        logHistoryRepository.save(log);
    }

}
