package org.security_study.global.security.oauth.naver;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NaverToken(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("refresh_token") String refreshToken,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("expires_in") String expiresIn
) {
}

/**
 * 인가 코드로 Access Token을 받기 위한 응답 모델
 * 우리는 Access Token만 사용
 *
 * https://nid.naver.com/oauth2.0/token으로 보낸 요청에 대한 응답을 받는다.
 *
 * 필요한 값은 아래 주소 참고
 * https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#request-token-response
 */