package org.security_study.global.security.oauth.abstracts;

import org.security_study.domain.user.entity.SocialType;

// OAuth 요청을 위한 Client 인터페이스
public interface OAuthClient {
    SocialType socialType(); // Client의 반환 타입

    String requestAccessToken(OAuthLoginParams params); // 인가 코드를 기반으로 인증 API 요청 -> Access Token 받기

    OAuthUserInfo requestOAuthInfo(String accessToken); // Access Token를 사용해 리소스 서버로부터 사용자 정보 받기
}
