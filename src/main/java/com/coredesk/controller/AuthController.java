package com.coredesk.controller;

import com.coredesk.dto.AuthRequest;
import com.coredesk.dto.RestResponse;
import com.coredesk.model.User;
import com.coredesk.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/users")
    public RestResponse createUser(@RequestBody User user) {
        var data = authService.createUser(user);
        return new RestResponse(data);
    }

    @PostMapping("/login")
    public Object login(@RequestBody AuthRequest authRequest) {
        var data = authService.login(authRequest);
        return new RestResponse(data);
    }

}