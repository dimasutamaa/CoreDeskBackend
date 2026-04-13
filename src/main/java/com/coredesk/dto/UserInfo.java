package com.coredesk.dto;

import com.coredesk.enums.Role;
import lombok.Getter;
import lombok.Builder;

@Getter
@Builder
public class UserInfo {
    private Long id;
    private String username;
    private String email;
    private Role role;
}