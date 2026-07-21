package com.meerkatgramv2auth.domain.auth.service;

import com.meerkatgramv2auth.domain.auth.request.SignupRequestDTO;
import com.meerkatgramv2auth.domain.user.entity.User;
import com.meerkatgramv2auth.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signup(SignupRequestDTO requestDTO) {
        if (userRepository.existsByEmail(requestDTO.email())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        User user = User.builder()
            .email(requestDTO.email())
            .password(passwordEncoder.encode(requestDTO.password()))
            .nickname(requestDTO.nickname())
            .build();

        userRepository.save(user);
    }
}
