package org.security_study.domain.auth.service;

import io.jsonwebtoken.JwtException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.security_study.domain.auth.dto.LoginResultDto;
import org.security_study.domain.auth.exception.RefreshTokenMismatchException;
import org.security_study.domain.user.entity.UserInfo;
import org.security_study.domain.user.exception.UserNotFoundException;
import org.security_study.domain.user.repository.UserInfoRepository;
import org.security_study.global.security.jwt.dto.TokenDto;
import org.security_study.global.security.jwt.util.TokenProvider;
import org.security_study.global.security.oauth.abstracts.OAuthLoginParams;
import org.security_study.global.security.oauth.abstracts.OAuthUserInfo;
import org.security_study.global.security.oauth.service.RequestOAuthInfoService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserInfoRepository userInfoRepository;
    private final RequestOAuthInfoService requestOAuthInfoService;
    private final TokenProvider tokenProvider;

    @Transactional
    public LoginResultDto login(OAuthLoginParams params) {
        OAuthUserInfo oAuthUserInfo = requestOAuthInfoService.request(params);
        UserInfo userInfo = findOrCreateUser(oAuthUserInfo);

        TokenDto tokenDto = tokenProvider.generateToken(userInfo.getId());
        userInfo.updateRefreshToken(tokenDto.getRefreshToken());

        log.info(tokenDto.getAccessToken());

        return LoginResultDto.builder()
                .isNewUser(userInfo.getNickname() == null)
                .token(tokenDto)
                .build();
    }

    @Transactional
    public void signup(Long userId, String nickname) {
        UserInfo userInfo = userInfoRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        userInfo.updateAdditionalInfo(nickname);
    }

    @Transactional
    public TokenDto reissueToken(TokenDto tokenDto) {
        String refreshToken = tokenDto.getRefreshToken();

        //1. 리프레시 토큰 유효성 검사
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new JwtException("입력 받은 Refresh Token은 잘못되었습니다.");
        }

        //2. 리프레시 토큰 찾기
        UserInfo userInfo= userInfoRepository.findByRefreshToken(refreshToken);

        //3. 리프레시 토큰 검증
        if (!userInfo.getRefreshToken().equals(refreshToken)) {
            throw new RefreshTokenMismatchException("Refresh Token = " + refreshToken);
        }

        //4. 새로운 Access Token 생성
        return tokenProvider.generateAccessTokenByRefreshToken(userInfo.getId(), refreshToken);
    }

    private UserInfo findOrCreateUser(OAuthUserInfo oAuthUserInfo) {
        return userInfoRepository.findByEmail(oAuthUserInfo.getEmail())
                .orElseGet(() -> createNewUser(oAuthUserInfo));
    }

    private UserInfo createNewUser(OAuthUserInfo oAuthUserInfo) {
        UserInfo userInfo = new UserInfo(
                oAuthUserInfo.getEmail(),
                oAuthUserInfo.getSocialType()
        );

        return userInfoRepository.save(userInfo);
    }
}
