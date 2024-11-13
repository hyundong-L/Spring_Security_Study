package org.security_study.global.security.oauth.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoToken(
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("expires_in") String expiresIn,
        @JsonProperty("refresh_token") String refreshToken,
        @JsonProperty("refresh_token_expires_in") String refreshTokenExpiresIn,
        @JsonProperty("scope") String scope
) {
}

/**
 * 인가 코드로 Access Token을 받기 위한 응답 모델
 * 우리는 Access Token만 사용
 *
 * https://kauth.kakao.com/oauth/token으로 보낸 요청에 대한 응답을 받는다.
 *
 * 필요한 값은 아래 주소를 참고
 * https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#request-token-response
 */