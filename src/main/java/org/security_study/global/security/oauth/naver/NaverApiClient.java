package org.security_study.global.security.oauth.naver;

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
public class NaverApiClient implements OAuthClient {
    @Value("${oauth.naver.uri.auth}")
    private String authUrl;
    @Value("${oauth.naver.uri.api}")
    private String apiUrl;
    @Value("${oauth.naver.client-id}")
    private String clientId;
    @Value("${oauth.naver.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate;

    @Override
    public SocialType socialType() {
        return SocialType.NAVER;
    }

    @Override
    public String requestAccessToken(OAuthLoginParams params) {
        String url = authUrl + "/oauth2.0/token";
        HttpEntity<MultiValueMap<String, String>> request = generateHttpRequest(params);

        NaverToken naverToken = restTemplate.postForObject(url, request, NaverToken.class);
        Objects.requireNonNull(naverToken);

        return naverToken.accessToken();
    }

    @Override
    public OAuthUserInfo requestOAuthInfo(String accessToken) {
        String url = apiUrl + "/v1/nid/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String requestToken = "Bearer " + accessToken;
        headers.set("Authorization", requestToken);

        log.info("headers: { }" + headers.toString());

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        HttpEntity<?> request = new HttpEntity<>(body, headers);

        return restTemplate.postForObject(url, request, NaverUserInfo.class);
    }

    private HttpEntity<MultiValueMap<String, String>> generateHttpRequest(OAuthLoginParams params) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = params.makeBody();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);

        return new HttpEntity<>(body, headers);
    }
}

/**
 * "https://nid.naver.com/oauth2.0/token?
 *        grant_type=authorization_code (추가 필요 param)
 *        &client_id=jyvqXeaVOVmV (추가 필요 param)
 *        &client_secret=527300A0_COq1_XV33cf (추가 필요 param)
 *        &code=EIc5bFrl4RibFls (NaverLoginParams에 포함)
 *        1&state=9kgsGTfH4j7IyAkg" (NaverLoginParams에 포함)
 *
 * 아래 두 사이트 참고
 *  https://developers.naver.com/docs/login/api/api.md
 *  https://developers.naver.com/docs/login/profile/profile.md
 */