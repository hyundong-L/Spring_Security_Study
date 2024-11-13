package org.security_study.global.security.oauth.service;

import org.security_study.domain.user.entity.SocialType;
import org.security_study.global.security.oauth.abstracts.OAuthClient;
import org.security_study.global.security.oauth.abstracts.OAuthLoginParams;
import org.security_study.global.security.oauth.abstracts.OAuthUserInfo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RequestOAuthInfoService {
    private final Map<SocialType, OAuthClient> clients;

    public RequestOAuthInfoService(List<OAuthClient> clients) {
        this.clients = clients.stream().collect(
                Collectors.toUnmodifiableMap(OAuthClient::socialType, Function.identity())
        );
    }

    public OAuthUserInfo request(OAuthLoginParams params) {
        OAuthClient client = clients.get(params.socialType());
        String accessToken = client.requestAccessToken(params);

        return client.requestOAuthInfo(accessToken);
    }
}

/**
 * OAuthApiClient를 사용하는 서비스
 *
 * KakaoApiClient와 같이 각 서비스 별 직접 주입을 받으면 중복되는 코드가 많다.
 * 하지만 List<OAuthClient>로 주입 받고, Map으로 만들면 문제 해결 및 간단한 사용 가능
 *
 * 참고
 * List<인터페이스> 형식으로 주입 받으면 인터페이스의 구현체들이 모두 담겨 온다
 */