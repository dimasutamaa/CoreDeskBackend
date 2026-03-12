package com.coredesk.controller;

import com.coredesk.dto.FilterCriteria;
import com.coredesk.dto.RestResponse;
import com.coredesk.dto.TicketRequest;
import com.coredesk.service.TicketService;
import com.coredesk.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public RestResponse getTicketDetail(@PathVariable("ticketId") Long ticketId) {
        var data = ticketService.getTicketDetail(ticketId);
        return new RestResponse(data);
    }

    @GetMapping("/{ticketId}/checkUser")
    public RestResponse checkUser(@AuthenticationPrincipal UserDetails userDetails,
                                  @PathVariable("ticketId") Long ticketId,
                                  @RequestParam(value = "role", required = true) String role) {
        var data = ticketService.checkUser(userDetails.getUsername(), ticketId, role);
        return new RestResponse(data);
    }

    @GetMapping("/filters")
    public RestResponse getFilterOptions(@AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> data = new HashMap<>();
        data.put("statuses", List.of("OPEN", "IN_PROGRESS", "RESOLVED", "CLOSED"));
        data.put("priorities", List.of("LOW", "MEDIUM", "HIGH"));
        data.put("agents", userService.getUsersByRole("AGENT"));
        data.put("users", userService.getUsersByRole("USER"));
        return new RestResponse(data);
    }

}
