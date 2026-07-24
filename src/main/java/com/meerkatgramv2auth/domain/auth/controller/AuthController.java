package com.meerkatgramv2auth.domain.auth.controller;

import com.meerkatgramv2auth.domain.auth.request.LoginRequestDTO;
import com.meerkatgramv2auth.domain.auth.response.AuthResponseDTO;
import com.meerkatgramv2auth.domain.auth.service.AuthService;
import com.meerkatgramv2auth.global.config.openapi.CustomApiResponse;
import com.meerkatgramv2auth.global.response.GlobalResponseDTO;
import com.meerkatgramv2auth.global.response.constant.CustomResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "인증 API", description = "인증 담당")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "로그인 처리", description = "이메일과 비밀번호로 로그인")
    @SecurityRequirement()
    @CustomApiResponse(value = {
        CustomResponseCode.NOT_REGISTERED_ERROR,
        CustomResponseCode.INVALID_PARAMETER_ERROR,
        CustomResponseCode.DB_ERROR,
        CustomResponseCode.SYSTEM_ERROR
    })

    @PostMapping("/login")
    public ResponseEntity<GlobalResponseDTO<AuthResponseDTO>> login(
        @Valid @RequestBody LoginRequestDTO loginRequestDTO,
        HttpServletResponse response
    ) {
        return ResponseEntity.ok(GlobalResponseDTO.success(authService.login(response, loginRequestDTO)));
    }

    @Operation(summary = "로그아웃 처리")
    @CustomApiResponse(value = {
        CustomResponseCode.UNAUTHENTICATED_ERROR,
        CustomResponseCode.INVALID_TOKEN_ERROR,
        CustomResponseCode.DB_ERROR,
        CustomResponseCode.SYSTEM_ERROR
    })

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public ResponseEntity<GlobalResponseDTO<Void>> logout(
        HttpServletResponse response,
        Authentication authentication
    ) {
        long userId = Long.parseLong(authentication.getName());

        authService.logout(response, userId);

        return ResponseEntity.ok(GlobalResponseDTO.success());
    }

    @Operation(summary = "토큰 재발급 처리")
    @CustomApiResponse(value = {
        CustomResponseCode.INVALID_TOKEN_ERROR,
        CustomResponseCode.DB_ERROR,
        CustomResponseCode.SYSTEM_ERROR
    })
    @PostMapping("/reissue-token")
    public ResponseEntity<GlobalResponseDTO<AuthResponseDTO>> reissue(
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        return ResponseEntity.ok(GlobalResponseDTO.success(authService.reissue(request, response)));
    }
}
