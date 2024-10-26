package org.security_study.service;

import lombok.RequiredArgsConstructor;
import org.security_study.domain.UserInfo;
import org.security_study.repository.UserRepository;
import org.security_study.security.jwt.dto.TokenDto;
import org.security_study.security.jwt.util.TokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    @Transactional
    public TokenDto reissue(TokenDto tokenDto) {
        //Refresh Token 유효성 검사
        if (tokenProvider.validateToken(tokenDto.getRefreshToken())) {
            throw new RuntimeException("입력한 Refresh Token은 잘못된 토큰");
        }

        //Access token에서 userId 가져오기
        Authentication authentication = tokenProvider.getAuthentication(tokenDto.getAccessToken());
        Long userId = Long.valueOf(authentication.getName());

        UserInfo userInfo = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        //DB에 저장된 Refresh Token과 비교
        if (!userInfo.getRefreshToken().equals(tokenDto.getRefreshToken())) {
            throw new RuntimeException("일치하지 않는 Refresh Token");
        }

        return tokenProvider.generateAccessTokenByRefreshToken(userId, userInfo.getRole(), tokenDto.getRefreshToken());
    }

    @Transactional
    public void updateRefreshToken(Long userId, String refreshToken) {
        UserInfo userInfo = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));
        userInfo.updateRefreshToken(refreshToken);
    }
}
