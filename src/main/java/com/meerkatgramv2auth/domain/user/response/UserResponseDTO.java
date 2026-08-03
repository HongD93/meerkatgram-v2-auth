package com.meerkatgramv2auth.domain.user.response;

import com.meerkatgramv2auth.domain.user.entity.User;
import com.meerkatgramv2auth.global.security.constant.RolePolicy;

import java.time.LocalDateTime;

public record UserResponseDTO(
    long id
    , String email
    , String nick
    , RolePolicy role
    , String profile
    , LocalDateTime created_at
) {
    public static UserResponseDTO from(User user) {
        return new UserResponseDTO(
            user.getId()
            , user.getEmail()
            , user.getNick()
            , user.getRole()
            , user.getProfile()
            , user.getCreatedAt()
        );
    }
}
