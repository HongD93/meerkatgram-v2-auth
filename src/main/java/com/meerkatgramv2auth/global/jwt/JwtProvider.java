package com.meerkatgramv2auth.global.jwt;

import com.meerkatgramv2auth.domain.user.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtProvider {
    private final com.meerkatgramv2auth.global.jwt.JwtConfig jwtConfig;
    private final SecretKey secretKey;

    public JwtProvider(com.meerkatgramv2auth.global.jwt.JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtConfig.secret()));
    }

    public String generateAccessToken(User user) {
        return this.generateToken(user, jwtConfig.accessTokenExpiry());
    }

    public String generateRefreshToken(User user) {
        return this.generateToken(user, jwtConfig.refreshTokenCookieExpiry());
    }

    private String generateToken(User user, int expiry) {
        Date now = new Date();

        return Jwts.builder()
            .header() // 헤더를 셋팅하겠다.
            .type(jwtConfig.type()) // 토큰 유형
            .and()
            .subject(String.valueOf(user.getId())) // sub 셋팅
            .issuer(jwtConfig.issuer()) // 토큰 발급자 셋팅
            .issuedAt(now) // 토급 발급시간 설정
            .expiration(new Date(now.getTime() + expiry)) // 토큰 만료 시간 설정
            .claim("role", user.getRole()) // Private Claim 설정
            .signWith(secretKey) // 시그니쳐 작성
            .compact();
    }
}