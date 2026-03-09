package com.coredesk.controller;

import com.coredesk.dto.RestResponse;
import com.coredesk.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @GetMapping("/users/recap")
    public RestResponse getDataRecap(@AuthenticationPrincipal UserDetails userDetails) {
        var data = userService.getDataRecapByRole(userDetails.getUsername());
        return new RestResponse(data);
    }

}
