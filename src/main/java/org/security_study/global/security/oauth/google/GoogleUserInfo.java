package org.security_study.global.security.oauth.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import org.security_study.domain.user.entity.SocialType;
import org.security_study.global.security.oauth.abstracts.OAuthUserInfo;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleUserInfo implements OAuthUserInfo {
    private String email;

    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public SocialType getSocialType() {
        return SocialType.GOOGLE;
    }
}
