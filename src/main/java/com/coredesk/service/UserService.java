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

    public Map<String, Object> getDataRecapByRole(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        return switch (user.getRole()) {
            case "USER" -> getUserDataRecap(user.getId());
            case "ADMIN" -> getAdminDataRecap();
            default -> throw new AppException("Unsupported role: " + user.getRole(), HttpStatus.BAD_REQUEST);
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

    private Map<String, Object> getUserDataRecap(Long userId) {
        Map<String, Object> data = new HashMap<>();
        data.put("open", ticketRepository.countByCreatedBy_IdAndStatus(userId, TicketStatus.OPEN));
        data.put("closed", ticketRepository.countByCreatedBy_IdAndStatus(userId, TicketStatus.CLOSED));
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

}
