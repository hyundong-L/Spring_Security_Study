package org.security_study.global.security.oauth.naver;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.security_study.domain.user.entity.SocialType;
import org.security_study.global.security.oauth.abstracts.OAuthUserInfo;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class NaverUserInfo implements OAuthUserInfo {
    @JsonProperty("response")
    private Response response;

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Response(String email) {}

    @Override
    public String getEmail() {
        return response.email;
    }

    @Override
    public SocialType getSocialType() {
        return SocialType.NAVER;
    }
}

/**
 * https://openapi.naver.com/v1/nid/me로 요청했을 때 받는 값들.
 *
 * 받는 값들은 아래 주소 참고
 * https://developers.naver.com/docs/login/profile/profile.md
 *
 *
 * @JsonProperty, @JsonIgnoreProperties는 KakaoUserInfo의 주석 참고
 */