package com.coredesk.controller;

import com.coredesk.dto.FilterCriteria;
import com.coredesk.dto.RestResponse;
import com.coredesk.dto.TicketRequest;
import com.coredesk.service.TicketService;
import com.coredesk.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RestResponse createTicket(@Valid @RequestBody TicketRequest request) {
        var data = ticketService.createTicket(request);
        return new RestResponse(data);
    }

    @GetMapping
    public RestResponse getUserTickets(@AuthenticationPrincipal UserDetails userDetails, FilterCriteria filter) {
        var data = ticketService.getUserTickets(userDetails.getUsername(), filter);
        return new RestResponse(data);
    }

    @GetMapping("/{ticketId}")
    public RestResponse getTicketDetail(@AuthenticationPrincipal UserDetails userDetails,
                                        @PathVariable("ticketId") Long ticketId,
                                        @RequestParam(value = "role", required = false) String role) {
        var data = ticketService.getTicketDetail(userDetails.getUsername(), ticketId, role);
        return new RestResponse(data);
    }

    @PutMapping("/{ticketId}/process")
    public RestResponse processTicket(@AuthenticationPrincipal UserDetails userDetails,
                                  @PathVariable("ticketId") Long ticketId,
                                  @RequestBody Map<String, Object> body,
                                  @RequestParam(value = "role", required = true) String role) {
        ticketService.processTicket(userDetails.getUsername(), ticketId, body, role);
        return new RestResponse();
    }

    @GetMapping("/filters")
    public RestResponse getFilterOptions() {
        Map<String, Object> data = new HashMap<>();
        data.put("statuses", List.of("OPEN", "ASSIGNED", "IN_PROGRESS", "RESOLVED", "CLOSED"));
        data.put("priorities", List.of("LOW", "MEDIUM", "HIGH"));
        data.put("agents", userService.getUsersByRole("AGENT"));
        data.put("users", userService.getUsersByRole("USER"));
        return new RestResponse(data);
    }

}
