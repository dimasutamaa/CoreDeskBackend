package com.coredesk.mapper;

import com.coredesk.dto.UserInfo;
import com.coredesk.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserInfo toDto(User user) {
        if (user == null) return null;

        return UserInfo.builder()
                .id(user.getId())
                .username(user.getDisplayName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

}
