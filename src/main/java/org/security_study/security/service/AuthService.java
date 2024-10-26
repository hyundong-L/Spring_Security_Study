package org.security_study.security.service;

import lombok.RequiredArgsConstructor;
import org.security_study.domain.UserInfo;
import org.security_study.domain.enums.Authority;
import org.security_study.dto.auth.LoginRequestDto;
import org.security_study.dto.auth.SignupRequestDto;
import org.security_study.dto.auth.SignupResponseDto;
import org.security_study.dto.token.TokenDto;
import org.security_study.repository.UserRepository;
import org.security_study.security.jwt.TokenProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public TokenDto login(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequestDto.getLoginId(), loginRequestDto.getPassword()));

        UserInfo userInfo = userRepository.findByLoginId(loginRequestDto.getLoginId())
                .orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다."));

        TokenDto tokenDto = tokenProvider.generateToken(authentication);
        userInfo.updateRefreshToken(tokenDto.getRefreshToken());

        return tokenDto;
    }

    @Transactional
    public SignupResponseDto signup(SignupRequestDto signupRequestDto) {
        userRepository.findByLoginId(signupRequestDto.getLoginId())
                .ifPresent(user -> {
                    throw new RuntimeException("이미 존재하는 사용자입니다.");
                });

        UserInfo newUserInfo = userRepository.save(
                UserInfo.builder()
                        .loginId(signupRequestDto.getLoginId())
                        .password(passwordEncoder.encode(signupRequestDto.getPassword()))
                        .username(signupRequestDto.getUsername())
                        .authority(Authority.ROLE_USER)
                        .build()
        );

        return SignupResponseDto.builder()
                .userId(newUserInfo.getId())
                .loginId(newUserInfo.getLoginId())
                .username(newUserInfo.getUsername())
                .build();
    }

    @Transactional
    public TokenDto reissue(TokenDto tokenDto) {
        if (!tokenProvider.validateToken(tokenDto.getRefreshToken())) {
            throw new RuntimeException("유효하지 않은 Refresh Token");
        }

        UserInfo userInfo = userRepository.findByRefreshToken(tokenDto.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("로그아웃 된 사용자"));

        if (!userInfo.getRefreshToken().equals(tokenDto.getRefreshToken())) {
            throw new RuntimeException("저장된 Refresh Token이 일치하지 않습니다.");
        }

        return tokenProvider.generateAccessTokenByRefreshToken(
                userInfo.getId().toString(),
                userInfo.getAuthority().toString(),
                userInfo.getRefreshToken()
        );
    }
}
