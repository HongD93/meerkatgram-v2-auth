package com.meerkatgramv2auth.global.security.oauth2;

import com.meerkatgramv2auth.domain.auth.service.KakaoOAuth2Service;
import com.meerkatgramv2auth.global.response.constant.CustomResponseCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DelegatingOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final KakaoOAuth2Service kakaoOAuth2Service;

    @Override
    public @Nullable OAuth2User loadUser(@NonNull OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // OAuth의 registrationId 획득
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        return switch(registrationId) {
            case "kakao" -> kakaoOAuth2Service.loadUser(userRequest);
            default -> throw new OAuth2AuthenticationException(
                new OAuth2Error(
                    CustomResponseCode.UNSUPPORTED_PROVIDER_ERROR.getCode(),
                    CustomResponseCode.UNSUPPORTED_PROVIDER_ERROR.name(),
                    null
                )
            );
        };
    }
}
