package com.coredesk.controller;

import com.coredesk.dto.RestResponse;
import com.coredesk.dto.TicketRequest;
import com.coredesk.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/tickets")
    @ResponseStatus(HttpStatus.CREATED)
    public RestResponse createTicket(@Valid @RequestBody TicketRequest request) {
        var data = ticketService.createTicket(request);
        return new RestResponse(data);
    }

    @GetMapping("/tickets")
    public RestResponse getUserTickets(@AuthenticationPrincipal UserDetails userDetails) {
        var data = ticketService.getUserTickets(userDetails.getUsername());
        return new RestResponse(data);
    }

}
