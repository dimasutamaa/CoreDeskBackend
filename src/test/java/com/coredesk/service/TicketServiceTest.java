package com.coredesk.service;

import com.coredesk.dto.TicketRequest;
import com.coredesk.enums.TicketStatus;
import com.coredesk.model.Ticket;
import com.coredesk.model.User;
import com.coredesk.repository.LogHistoryRepository;
import com.coredesk.repository.TicketRepository;
import com.coredesk.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LogHistoryRepository logHistoryRepository;

    @InjectMocks
    private TicketService ticketService;

    @Test
    void createTicket_shouldLogAndRethrow_whenLogHistoryFails() {
        // arrange
        TicketRequest request = new TicketRequest();
        request.setCreatedBy(1L);

        User mockUser = new User();
        mockUser.setId(1L);

        Ticket savedTicket = new Ticket();
        savedTicket.setId(1L);
        savedTicket.setStatus(TicketStatus.OPEN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(ticketRepository.save(any())).thenReturn(savedTicket);
        when(logHistoryRepository.save(any())).thenThrow(new RuntimeException("Log write failed")); // simulate log failure

        // act & assert
        assertThrows(RuntimeException.class, () -> ticketService.createTicket(request));
    }

}
