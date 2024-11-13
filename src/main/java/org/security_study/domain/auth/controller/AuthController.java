package org.security_study.domain.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.security_study.domain.auth.dto.LoginResultDto;
import org.security_study.domain.auth.service.AuthService;
import org.security_study.global.security.jwt.dto.TokenDto;
import org.security_study.global.security.oauth.google.GoogleLoginParams;
import org.security_study.global.security.oauth.kakao.KakaoLoginParam;
import org.security_study.global.security.oauth.naver.NaverLoginParam;
import org.security_study.global.template.ResponseTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping("/naver")
    public ResponseTemplate<LoginResultDto> naverLogin(@RequestBody NaverLoginParam naverLoginParam) {
        return new ResponseTemplate<>(HttpStatus.OK, "네이버 로그인 성공", authService.login(naverLoginParam));
    }

    @PostMapping("/kakao")
    public ResponseTemplate<LoginResultDto> kakaoLogin(@RequestBody KakaoLoginParam kakaoLoginParam) {
        log.info(kakaoLoginParam.getAuthorizationCode());
        return new ResponseTemplate<>(HttpStatus.OK, "카카오 로그인 성공", authService.login(kakaoLoginParam));
    }

    @PostMapping("/google")
    public ResponseTemplate<LoginResultDto> googleLogin(@RequestBody GoogleLoginParams googleLoginParam) {
        log.info(googleLoginParam.getAuthorizationCode());
        return new ResponseTemplate<>(HttpStatus.OK, "구글 로그인 성공", authService.login(googleLoginParam));
    }

    @PatchMapping("/signup")
    public ResponseTemplate<?> signup(
            @AuthenticationPrincipal Long userId,
            @RequestParam("nickname") String nickname
    ) {
        authService.signup(userId, nickname);
        return new ResponseTemplate<>(HttpStatus.OK, "회원가입 성공");
    }

    @PostMapping("/reissue")
    public ResponseTemplate<TokenDto> reissue(@RequestBody TokenDto tokenDto) {
        return new ResponseTemplate<>(HttpStatus.OK, "엑세스 토큰 재발급", authService.reissueToken(tokenDto));
    }
}
