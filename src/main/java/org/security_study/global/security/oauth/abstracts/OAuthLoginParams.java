package org.security_study.global.security.oauth.abstracts;

import org.security_study.domain.user.entity.SocialType;
import org.springframework.util.MultiValueMap;

// OAuth2 플랫폼 요청에 필요한 데이터를 갖고 있는 파라미터 값들을 담는 메서드 추상화
public interface OAuthLoginParams {
    SocialType socialType();
    MultiValueMap<String, String> makeBody();
}
