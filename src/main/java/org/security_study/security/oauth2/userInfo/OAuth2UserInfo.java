package org.security_study.security.oauth2.userInfo;

import lombok.AllArgsConstructor;

import java.util.Map;

/*
oauth2에서 활용할 정보들을 추상 클래스로 정의.
각 oauth2 userinfo 클래스들에서 이를 구현하도록 함

만약 아래 메소드 이외의 더 필요한 것이 있다면 추가하면 된다.
 */
@AllArgsConstructor
public abstract class OAuth2UserInfo {
    protected Map<String, Object> attributes;   //추상 클래스를 상속 받는 클래스에서만 사용 가능

    public abstract String getEmail();
}
