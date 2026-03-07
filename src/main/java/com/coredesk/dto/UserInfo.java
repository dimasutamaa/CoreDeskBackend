package com.coredesk.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserInfo {
    private Long id;
    private String username;
    private String email;
    private String role;
}