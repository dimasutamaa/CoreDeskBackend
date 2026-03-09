package com.coredesk.service;

import com.coredesk.dto.TicketRequest;
import com.coredesk.enums.TicketStatus;
import com.coredesk.exception.AppException;
import com.coredesk.model.LogHistory;
import com.coredesk.model.Ticket;
import com.coredesk.model.User;
import com.coredesk.repository.LogHistoryRepository;
import com.coredesk.repository.TicketRepository;
import com.coredesk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
            createLogHistory(savedTicket, user, savedTicket.getStatus(), "Ticket created");
            return savedTicket;
        } catch (Exception e) {
            log.error("Failed to create ticket: {}", e.getMessage(), e);
            throw e;
        }
    }

    public List<Ticket> getUserTickets(String email) {
        return ticketRepository.findByCreatedBy_EmailOrderByCreatedAtDesc(email);
    }

    private void createLogHistory(Ticket ticket, User user, TicketStatus status, String description) {
        LogHistory log = new LogHistory();
        log.setTicket(ticket);
        log.setCreatedBy(user);
        log.setStatus(status.name());
        log.setDescription(description);

        logHistoryRepository.save(log);
    }

}
