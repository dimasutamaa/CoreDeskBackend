package com.coredesk.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AuthRequest {
    private String email;
    private String password;
}