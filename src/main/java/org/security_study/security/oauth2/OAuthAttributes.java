package org.security_study.security.oauth2;

import lombok.Builder;
import lombok.Getter;
import org.security_study.domain.SocialType;
import org.security_study.security.oauth2.userInfo.GoogleOAuth2UserInfo;
import org.security_study.security.oauth2.userInfo.KakaoOAuth2UserInfo;
import org.security_study.security.oauth2.userInfo.NaverOAuth2UserInfo;
import org.security_study.security.oauth2.userInfo.OAuth2UserInfo;

import java.util.Map;

/*
각 소셜 로그인을 진행 후 받는 데이터가 다르기 때문에, 각 소셜에 따른 데이터를 분기 처리하는 DTO 클래스
 */
@Getter
public class OAuthAttributes {
    private String nameAttributeKey;    //사용자의 고유 식별자를 나타내는 키의 이름 저장 - Ex. Naver: "id"
    private OAuth2UserInfo oAuth2UserInfo;

    @Builder
    private OAuthAttributes(String nameAttributeKey, OAuth2UserInfo oAuth2UserInfo) {
        this.nameAttributeKey = nameAttributeKey;
        this.oAuth2UserInfo = oAuth2UserInfo;
    }

    public static OAuthAttributes of(SocialType socialType, String userNameAttributeName, Map<String, Object> attributes) {
        if (socialType.equals(SocialType.NAVER)) {
            return ofNaver(userNameAttributeName, attributes);
        } else if (socialType.equals(SocialType.GOOGLE)) {
            return ofGoogle(userNameAttributeName, attributes);
        } else {
            return ofKakao(userNameAttributeName, attributes);
        }
    }

    private static OAuthAttributes ofNaver(String nameAttributeKey, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(nameAttributeKey)
                .oAuth2UserInfo(new NaverOAuth2UserInfo(attributes))
                .build();
    }

    private static OAuthAttributes ofGoogle(String nameAttributeKey, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(nameAttributeKey)
                .oAuth2UserInfo(new GoogleOAuth2UserInfo(attributes))
                .build();
    }

    private static OAuthAttributes ofKakao(String nameAttributeKey, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(nameAttributeKey)
                .oAuth2UserInfo(new KakaoOAuth2UserInfo(attributes))
                .build();
    }
}
