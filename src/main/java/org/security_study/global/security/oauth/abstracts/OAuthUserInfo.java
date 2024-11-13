package org.security_study.global.security.oauth.abstracts;

import org.security_study.domain.user.entity.SocialType;

// Access Token을 사용해 리소스 서버로 사용자 프로필 정보 요청을 보내고, 받기 위해 사용하는 메서드 추상화
public interface OAuthUserInfo {
    String getEmail();
    SocialType getSocialType();
}
