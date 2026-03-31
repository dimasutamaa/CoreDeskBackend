package com.coredesk.controller;

import com.coredesk.dto.RestResponse;
import com.coredesk.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{ticketId}")
    public RestResponse addComment(@AuthenticationPrincipal UserDetails userDetails,
                                   @PathVariable("ticketId") Long ticketId,
                                   @RequestBody Map<String, Object> body) {
        commentService.addComment(userDetails.getUsername(), ticketId, body);
        return new RestResponse();
    }

    @GetMapping("/{ticketId}")
    public RestResponse getTicketComments(@PathVariable("ticketId") Long ticketId) {
        var data = commentService.getTicketComments(ticketId);
        return new RestResponse(data);
    }

    @DeleteMapping("/{commentId}")
    public RestResponse deleteComment(@AuthenticationPrincipal UserDetails userDetails,
                                      @PathVariable("commentId") Long commentId) {
        commentService.deleteComment(userDetails.getUsername(), commentId);
        return new RestResponse();
    }

}
