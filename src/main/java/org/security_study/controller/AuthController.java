package org.security_study.controller;

import lombok.RequiredArgsConstructor;
import org.security_study.dto.ProfileUpdateRequestDto;
import org.security_study.response.ResponseTemplate;
import org.security_study.security.jwt.dto.TokenDto;
import org.security_study.service.AuthService;
import org.security_study.service.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final TokenService tokenService;

    /*
    < OAuth2 >
    1. 소셜 로그인 성공 시(OAuth2LoginSuccessHandler까지 실행 후), 프론트에서는 Access Token을 헤더에 추가.
        * 우리는 이전에 로그인 했던 회원인지 판별 후 다르게 주소를 반환하는데, 대신 반환 값으로 Boolean 값을 추가하여 프론트에서 처리하는 방법도 있다.
    if: 첫 로그인 회원이라면
        2. 회원가입 페이지로 이동 후, 프론트에서 /oauth2/signup으로 api 요청 보냄
        3. 회원가입 성공 후 AccessToken을 다시 발급하면, 프론트에서는 새로 발급한 Access Token을 헤더에 추가
        4. 프론트에서 메인 페이지로 이동 후 원하는 서비스 이용
    else:
        2. 프론트에서 메인 페이지로 이동 후 원하는 서비스 이용
     */
    @PostMapping("/signup")
    public ResponseTemplate<TokenDto> signup(@RequestBody ProfileUpdateRequestDto profileUpdateRequestDto) throws IOException {
        return new ResponseTemplate<>(HttpStatus.CREATED, "회원가입 성공", authService.signup(profileUpdateRequestDto));
    }

    /*
    < Access Token 재발급 >
    1. 로그인 진행 후 백엔드에서 Access, Refresh Token 생성 및 프론트로 반환
    2. 프론트에서는 api 요청을 보낼 때마다 헤더에 Access Token을 담아서 보냄
    3. 만약 백엔드에서 Access Token이 만료되었다는 예외를 던진다면, 프론트에서는 Access, Refresh Token을 Dto에 담아 재발급 요청(Body에 담아서 보내는 것. 헤더에는 담을 필요 없다)
    4. 백엔드에서 받은 토큰들 검사 후 재발급 - reissue() 코드 참고
        만약 검사를 통과하지 못하면 재로그인 필요.
     */
    @PostMapping("/reissue")
    public ResponseTemplate<TokenDto> reissue(@RequestBody TokenDto tokenDto) {
        return new ResponseTemplate<>(HttpStatus.OK, "엑세스 토큰 재발급", tokenService.reissue(tokenDto));
    }
}
