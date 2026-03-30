package com.coredesk.dto;

import lombok.Getter;
import lombok.Builder;

@Getter
@Builder
public class UserInfo {
    private Long id;
    private String username;
    private String email;
    private String role;
}