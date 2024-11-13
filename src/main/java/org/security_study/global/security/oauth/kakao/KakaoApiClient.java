package org.security_study.global.security.oauth.kakao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.security_study.domain.user.entity.SocialType;
import org.security_study.global.security.oauth.abstracts.OAuthClient;
import org.security_study.global.security.oauth.abstracts.OAuthLoginParams;
import org.security_study.global.security.oauth.abstracts.OAuthUserInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoApiClient implements OAuthClient {
    @Value("${oauth.kakao.url.auth}")
    private String authUrl;
    @Value("${oauth.kakao.url.api}")
    private String apiUrl;
    @Value("${oauth.kakao.client-id}")
    private String clientId;
    @Value("${oauth.kakao.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate;

    @Override
    public SocialType socialType() {
        return SocialType.KAKAO;
    }

    @Override
    public String requestAccessToken(OAuthLoginParams params) {
        String url = authUrl + "/oauth/token";
        HttpEntity<MultiValueMap<String, String>> request = generateHttpRequest(params);

        KakaoToken kakaoToken = restTemplate.postForObject(url, request, KakaoToken.class);
        Objects.requireNonNull(kakaoToken, "Kakao token is null");

        return kakaoToken.accessToken();
    }

    @Override
    public OAuthUserInfo requestOAuthInfo(String accessToken) {
        String url = apiUrl + "/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + accessToken);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("property_keys", "[\"kakao_account.email\"]");

        log.info("body_info={}", body);

        HttpEntity<?> request = new HttpEntity<>(body, headers);

        return restTemplate.postForObject(url, request, KakaoUserInfo.class);
    }

    private HttpEntity<MultiValueMap<String, String>> generateHttpRequest(OAuthLoginParams params) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = params.makeBody();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);

        log.info("body_token={}", body);

        return new HttpEntity<>(body, headers);
    }
}

/**
 * https://kauth.kakao.com/oauth/token?
 * 	    grant_type=authorization_code (추가 필요 param)
 * 		&client_id=${REST_API_KEY} (추가 필요 param)
 * 		&redirect_uri=${REDIRECT_URI} (KaKaoLoginParams에 포함)
 * 		&code=${code} (KaKaoLoginParams에 포함)
 *
 * 아래 두 사이트 참고
 * 	https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#request-token
 * 	https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#req-user-info
 */
