package org.security_study.security.oauth2.userInfo;

import java.util.Map;

public class GoogleOAuth2UserInfo extends OAuth2UserInfo {
    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }
}

/*
- 구글의 유저 정보 Response JSON 예시 -
{
   "sub": "식별값",
   "name": "name",
   "given_name": "given_name",
   "picture": "https//lh3.googleusercontent.com/~~",
   "email": "email",
   "email_verified": true,
   "locale": "ko"
}
 */