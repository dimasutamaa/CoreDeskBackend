package com.coredesk.service;

import com.coredesk.dto.CommentResponse;
import com.coredesk.exception.AppException;
import com.coredesk.mapper.CommentMapper;
import com.coredesk.model.Comment;
import com.coredesk.model.Ticket;
import com.coredesk.model.User;
import com.coredesk.repository.CommentRepository;
import com.coredesk.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final TicketRepository ticketRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final CommentMapper commentMapper;

    public void addComment(String email, Long ticketId, Map<String, Object> body) {
        User user = userService.getUserByEmail(email);

        if (!ticketRepository.existsById(ticketId)) {
            throw new AppException("Ticket not found", HttpStatus.NOT_FOUND);
        }

        String message = (body.get("message") != null ? body.get("message").toString() : null);
        if (message == null || message.isBlank()) {
            throw new AppException("Message cannot be null or blank", HttpStatus.BAD_REQUEST);
        }

        Comment comment = Comment.builder()
                .message(message)
                .ticketId(ticketId)
                .user(user)
                .build();

        commentRepository.save(comment);
    }

    public List<CommentResponse> getTicketComments(Long ticketId) {
        Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);

        if (ticketOpt.isPresent()) {
            return commentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId)
                    .stream()
                    .map(commentMapper::toDto)
                    .toList();
        }

        return List.of();
    }

    public void deleteComment(String email, Long commentId) {
        User user = userService.getUserByEmail(email);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException("Comment not found", HttpStatus.NOT_FOUND));

        if (!comment.getUser().equals(user)) {
            throw new AppException("Not authorized to delete this comment", HttpStatus.FORBIDDEN);
        }

        commentRepository.delete(comment);
    }

}
