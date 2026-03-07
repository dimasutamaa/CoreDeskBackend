package com.coredesk.controller;

import com.coredesk.dto.RestResponse;
import com.coredesk.dto.TicketRequest;
import com.coredesk.exception.AppException;
import com.coredesk.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/tickets")
    public RestResponse createTicket(@Valid @RequestBody TicketRequest request) throws AppException {
        var data = ticketService.createTicket(request);
        return new RestResponse(data);
    }

}
