package com.coredesk.controller;

import com.coredesk.dto.RestResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/dashboard")
    public RestResponse adminDashboard() {
        return new RestResponse("Test admin endpoint");
    }

}
