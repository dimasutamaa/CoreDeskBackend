package com.coredesk.service;

import com.coredesk.enums.TicketStatus;
import com.coredesk.exception.AppException;
import com.coredesk.model.User;
import com.coredesk.repository.TicketRepository;
import com.coredesk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;

    public Map<String, Object> getDataRecapByRole(String email, String role) {
        return switch (role) {
            case "USER" -> getUserDataRecap(email);
            case "ADMIN" -> getAdminDataRecap();
            case "AGENT" -> getAgentDataRecap(email);
            default -> throw new AppException("Unsupported role: " + role, HttpStatus.BAD_REQUEST);
        };
    }

    public Map<Long, String> getUsersByRole(String role) {
        List<User> users = userRepository.findByRole(role);
        return users.stream().collect(
                Collectors.toMap(
                        User::getId,
                        User::getDisplayName
                )
        );
    }

    private Map<String, Object> getUserDataRecap(String email) {
        Map<String, Object> data = new HashMap<>();
        data.put("open", ticketRepository.countByCreatedBy_EmailAndStatus(email, TicketStatus.OPEN));
        data.put("closed", ticketRepository.countByCreatedBy_EmailAndStatus(email, TicketStatus.CLOSED));
        return data;
    }

    private Map<String, Object> getAdminDataRecap() {
        Map<String, Object> data = new HashMap<>();
        data.put("open", ticketRepository.countByStatus(TicketStatus.OPEN));
        data.put("inProgress", ticketRepository.countByStatus(TicketStatus.IN_PROGRESS));
        data.put("resolved", ticketRepository.countByStatus(TicketStatus.RESOLVED));
        data.put("closed", ticketRepository.countByStatus(TicketStatus.CLOSED));

        data.put("totalUsers", userRepository.countByRole("USER"));
        data.put("admins", userRepository.countByRole("ADMIN"));
        data.put("agents", userRepository.countByRole("AGENT"));
        return data;
    }

    private Map<String, Object> getAgentDataRecap(String email) {
        Map<String, Object> data = new HashMap<>();
        data.put("assigned", ticketRepository.countByAssignedTo_EmailAndStatus(email, TicketStatus.ASSIGNED));
        data.put("inProgress", ticketRepository.countByAssignedTo_EmailAndStatus(email, TicketStatus.IN_PROGRESS));
        data.put("resolved", ticketRepository.countByAssignedTo_EmailAndStatus(email, TicketStatus.RESOLVED));
        return data;
    }

}
