package com.meerkatgramv2auth.domain.auth.service;

import com.meerkatgramv2auth.domain.auth.repository.AuthRepository;
import com.meerkatgramv2auth.domain.auth.request.LoginRequestDTO;
import com.meerkatgramv2auth.domain.auth.request.RegistrationRequestDTO;
import com.meerkatgramv2auth.domain.auth.response.AuthResponseDTO;
import com.meerkatgramv2auth.domain.user.entity.User;
import com.meerkatgramv2auth.global.cookie.CookieManager;
import com.meerkatgramv2auth.global.error.custom.InvalidTokenException;
import com.meerkatgramv2auth.global.error.custom.NotRegisteredException;
import com.meerkatgramv2auth.global.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final CookieManager cookieManager;

    @Transactional(rollbackFor = Exception.class)
    public AuthResponseDTO login(HttpServletResponse response, LoginRequestDTO loginRequestDTO) {
        // 유저 정보를 획득 & 유저 가입 여부를 체크
        User user = authRepository.findByEmail(loginRequestDTO.email())
            .orElseThrow(() -> new NotRegisteredException("아이디와 비밀번호를 확인해주세요."));

        // 비밀번호 체크
        if(!passwordEncoder.matches(loginRequestDTO.password(), user.getPassword())) {
            throw new NotRegisteredException("아이디와 비밀번호를 확인해주세요.");
        }

        return this.generateAuthentication(response, user);
    }

    @Transactional(rollbackFor = Exception.class)
    public AuthResponseDTO reissue(HttpServletRequest request, HttpServletResponse response) {
        // 쿠키 리프레시 토큰 획득
        String refreshToken = cookieManager.getRefreshTokenToCookie(request)
            .orElseThrow(() -> new InvalidTokenException("리프레시 토큰 없음"));

        long userId = Long.parseLong(jwtProvider.extractClaims(refreshToken).getSubject());

        // 유저 획득 및 가입 여부 확인
        User user = authRepository.findById(userId)
            .orElseThrow(() -> new InvalidTokenException("유효하지 않은 회원의 토큰입니다."));

        // 비로그인 상태 확인
        if(user.getRefreshToken() == null) {
            throw new InvalidTokenException("비로그인 상태입니다.");
        }

        // 리프레시 토큰 일치 확인
        if(!refreshToken.equals(user.getRefreshToken())) {
            throw new InvalidTokenException("토큰 불일치입니다.");
        }

        // 인증 정보 생성 및 리턴
        return this.generateAuthentication(response, user);
    }

    private AuthResponseDTO generateAuthentication(HttpServletResponse response, User user) {
        // 토큰생성
        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);

        // 리프래시토큰 DB 저장 처리
        user.setRefreshToken(refreshToken);
        authRepository.save(user);

        // 리프래시 토큰 cookie에 저장
        cookieManager.setRefreshTokenToCookie(response, refreshToken);

        return AuthResponseDTO.from(user, accessToken);
    }

    @Transactional(rollbackFor = Exception.class)
    public void logout(HttpServletResponse response, long userId) {
        // 유저 정보 획득
        User user = authRepository.findById(userId)
            .orElseThrow(() -> new InvalidTokenException("유효하지 않은 회원입니다."));

        // DB에 저장한 리프레시 토큰 파기
        user.setRefreshToken(null);
        authRepository.save(user);

        // Cookie에 저장한 리프레시 토큰 파기
        cookieManager.removeRefreshTokenToCookie(response);
    }

    @Transactional(rollbackFor = Exception.class)
    public void registration(RegistrationRequestDTO registrationRequestDTO) {
        User user = new User();
        user.setEmail(registrationRequestDTO.email());
        user.setPassword(passwordEncoder.encode(registrationRequestDTO.password()));
        user.setNick(registrationRequestDTO.nick());
        user.setProfile(registrationRequestDTO.profile());
        authRepository.save(user);
    }
}
