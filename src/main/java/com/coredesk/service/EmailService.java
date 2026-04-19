package com.coredesk.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Async
    public void sendRejectNotification(List<String> emails, Map<String, Object> data) {
        String ticketId = data.get("ticketId").toString();
        String title = data.get("title").toString();
        String notes = data.get("notes").toString();

        log.info("Start sending email notification");
        for (String email : emails) {
            log.info("Sending email to {}", email);
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(email);
                message.setSubject("Ticket Rejected - " + ticketId);

                message.setText(
                        "Ticket has been rejected.\n\n" +
                        "Ticket ID: " + ticketId + "\n" +
                        "Title: " + title + "\n" +
                        "Notes: " + notes + "\n\n" +
                        "Please review and reprocess."
                );

                javaMailSender.send(message);

            } catch (Exception e) {
                log.error("Failed to send email to {}", email, e);
            }
        }
        log.info("Finish sending email notification");
    }

    @Async
    public void sendResolvedNotification(List<String> emails, Map<String, Object> data) {
        String ticketId = data.get("ticketId").toString();
        String title = data.get("title").toString();

        log.info("Start sending email notification");
        for (String email : emails) {
            log.info("Sending email to {}", email);

            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(email);
                message.setSubject("Ticket Resolved - " + ticketId);

                message.setText(
                        "Ticket has been resolved.\n\n" +
                        "Ticket ID: " + ticketId + "\n" +
                        "Title: " + title + "\n\n" +
                        "Waiting for admin confirmation."
                );

                javaMailSender.send(message);

            } catch (Exception e) {
                log.error("Failed to send email to {}", email, e);
            }
        }
        log.info("Finish sending email notification");
    }

}
