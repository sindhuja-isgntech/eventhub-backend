package com.isigntech.eventhub.dto;

import com.isigntech.eventhub.entity.User;

public record AuthUserResponse(Long id, String name, String email, String phone, String role) {
    public static AuthUserResponse from(User user) {
        return new AuthUserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole()
        );
    }
}
