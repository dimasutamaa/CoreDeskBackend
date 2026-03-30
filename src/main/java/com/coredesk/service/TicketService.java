package com.coredesk.service;

import com.coredesk.dto.CommentResponse;
import com.coredesk.dto.FilterCriteria;
import com.coredesk.dto.TicketRequest;
import com.coredesk.dto.TicketResponse;
import com.coredesk.enums.Priority;
import com.coredesk.enums.TicketStatus;
import com.coredesk.exception.AppException;
import com.coredesk.mapper.CommentMapper;
import com.coredesk.mapper.TicketMapper;
import com.coredesk.model.LogHistory;
import com.coredesk.model.Ticket;
import com.coredesk.model.User;
import com.coredesk.repository.CommentRepository;
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

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final LogHistoryRepository logHistoryRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final TicketMapper ticketMapper;
    private final CommentMapper commentMapper;

    @Transactional
    public Ticket createTicket(TicketRequest request) {
        User user = userRepository.findById(request.getCreatedBy())
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        Ticket ticket = new Ticket();
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setStatus(request.getStatus());
        ticket.setPriority(request.getPriority());
        ticket.setSlaDate(calculateSlaDate(request.getPriority()));
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

    public List<TicketResponse> getUserTickets(String email, FilterCriteria filter) {
        User user = userService.getUserByEmail(email);
        Specification<Ticket> ticketQuery = buildTicketQuery(user, filter);

        return ticketRepository.findAll(ticketQuery)
                .stream()
                .map(ticketMapper::toDto)
                .toList();
    }

    public Map<String, Object> getTicketDetail(String email, Long ticketId, String role) {
        User user = userService.getUserByEmail(email);
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new AppException("Ticket not found", HttpStatus.NOT_FOUND));

        if ("USER".equals(role) && !ticket.getCreatedBy().equals(user)) {
            throw new AppException("You cannot view the details of this ticket", HttpStatus.FORBIDDEN);
        }
        if ("AGENT".equals(role) && !ticket.getAssignedTo().equals(user)) {
            throw new AppException("This ticket belongs to another agent", HttpStatus.FORBIDDEN);
        }

        List<CommentResponse> comments = commentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId)
                .stream()
                .map(commentMapper::toDto)
                .toList();
        List<LogHistory> logHistories = logHistoryRepository.findByTicketIdOrderByCreatedAtDesc(ticketId);

        Map<String, Object> data = new HashMap<>();
        data.put("ticket", ticketMapper.toDto(ticket));
        data.put("comments", comments);
        data.put("logHistories", logHistories);

        return data;
    }

    @Transactional
    public void processTicket(String email, Long ticketId, Map<String, Object> body, String role) {
        User user = userService.getUserByEmail(email);
        Ticket ticket = ticketRepository.findForProcess(ticketId)
                .orElseThrow(() -> new AppException("Ticket not found", HttpStatus.NOT_FOUND));

        if ("ADMIN".equals(role)) handleAdminProcessTicket(ticket, user, body);
        else if ("AGENT".equals(role)) handleAgentProcessTicket(ticket, user);

        ticketRepository.save(ticket);
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
        } else if ("AGENT".equals(role)) {
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

    private void handleAdminProcessTicket(Ticket ticket, User user, Map<String, Object> body) {
        if (ticket.getProcessedBy() != null && !ticket.getProcessedBy().equals(user)) {
            throw new AppException("This ticket already processed by other user", HttpStatus.FORBIDDEN);
        }

        if (ticket.getStatus().equals(TicketStatus.OPEN)) {
            Long assignedTo = Long.valueOf(body.get("assignedTo").toString());
            User selectedUser = userRepository.findById(assignedTo)
                    .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

            ticket.setStatus(TicketStatus.ASSIGNED);
            ticket.setAssignedTo(selectedUser);
            ticket.setProcessedBy(user);

            createLogHistory(ticket.getId(), user.getDisplayName(),
                    ticket.getStatus(), "Ticket assigned to: " + selectedUser.getDisplayName());
        }
    }

    private void handleAgentProcessTicket(Ticket ticket, User user) {
        if (ticket.getStatus().equals(TicketStatus.ASSIGNED)) {
            ticket.setStatus(TicketStatus.IN_PROGRESS);
            createLogHistory(ticket.getId(), user.getDisplayName(), ticket.getStatus(), "Agent is working on the ticket");
        } else if (ticket.getStatus().equals(TicketStatus.IN_PROGRESS)) {
            ticket.setStatus(TicketStatus.RESOLVED);
            createLogHistory(ticket.getId(), user.getDisplayName(), ticket.getStatus(), "Ticket resolved");
        }
    }

    private LocalDateTime calculateSlaDate(Priority priority) {
        return switch (priority) {
            case Priority.LOW -> LocalDateTime.now().plusHours(72);
            case Priority.MEDIUM -> LocalDateTime.now().plusHours(24);
            case Priority.HIGH -> LocalDateTime.now().plusHours(8);
        };
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
