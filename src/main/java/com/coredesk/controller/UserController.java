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

    @GetMapping("/users")
    public RestResponse getUsersByRole(@RequestParam(value = "role", required = true) String role) {
        var data = userService.getUsersByRole(role);
        return new RestResponse(data);
    }

    @GetMapping("/users/recap")
    public RestResponse getDataRecap(@AuthenticationPrincipal UserDetails userDetails,
                                     @RequestParam(value = "role", required = true) String role) {
        var data = userService.getDataRecapByRole(userDetails.getUsername(), role);
        return new RestResponse(data);
    }

}
