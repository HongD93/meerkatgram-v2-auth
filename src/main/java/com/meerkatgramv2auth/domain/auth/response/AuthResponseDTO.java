package com.meerkatgramv2auth.domain.auth.response;

import com.meerkatgramv2auth.domain.user.entity.User;
import com.meerkatgramv2auth.domain.user.response.UserResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(defaultValue = "로그인 레스폰스")
public record AuthResponseDTO(
    UserResponseDTO user,
    String accessToken
) {
    public static AuthResponseDTO from(User user, String accessToken) {
        return new AuthResponseDTO(UserResponseDTO.from(user), accessToken);
    }
}
