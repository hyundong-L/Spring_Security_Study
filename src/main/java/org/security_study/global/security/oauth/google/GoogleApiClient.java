package org.security_study.global.security.oauth.google;

import lombok.RequiredArgsConstructor;
import org.security_study.domain.user.entity.SocialType;
import org.security_study.global.security.oauth.abstracts.OAuthClient;
import org.security_study.global.security.oauth.abstracts.OAuthLoginParams;
import org.security_study.global.security.oauth.abstracts.OAuthUserInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class GoogleApiClient implements OAuthClient {
    @Value("${oauth.google.client-id}")
    private String clientId;
    @Value("${oauth.google.client-secret}")
    private String clientSecret;
    @Value("${oauth.google.redirect-uri}")
    private String redirectUri;
    @Value("${oauth.google.token-uri}")
    private String tokenUri;
    @Value("${oauth.google.user-info-uri}")
    private String userInfoUri;

    private final RestTemplate restTemplate;

    @Override
    public SocialType socialType() {
        return SocialType.GOOGLE;
    }

    @Override
    public String requestAccessToken(OAuthLoginParams params) {
        HttpEntity<MultiValueMap<String, String>> request = generateHttpRequest(params);

        GoogleToken googleToken = restTemplate.postForObject(tokenUri, request, GoogleToken.class);
        Objects.requireNonNull(googleToken, "Google Token is null");

        return googleToken.accessToken();
    }

    @Override
    public OAuthUserInfo requestOAuthInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<?> request = new HttpEntity<>(headers);

        return restTemplate.exchange(userInfoUri, HttpMethod.GET, request, GoogleUserInfo.class).getBody();
    }

    private HttpEntity<MultiValueMap<String, String>> generateHttpRequest(OAuthLoginParams params) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = params.makeBody();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);

        return new HttpEntity<>(body, headers);
    }
}
