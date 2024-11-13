package org.security_study.global.security.oauth.naver;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.security_study.domain.user.entity.SocialType;
import org.security_study.global.security.oauth.abstracts.OAuthLoginParams;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NaverLoginParam implements OAuthLoginParams {
    private String authorizationCode;
    private String state;

    @Override
    public SocialType socialType() {
        return SocialType.NAVER;
    }

    @Override
    public MultiValueMap<String, String> makeBody() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", authorizationCode);
        body.add("state", state);

        return body;
    }
}
