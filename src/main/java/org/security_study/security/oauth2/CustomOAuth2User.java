package org.security_study.security.oauth2;

import lombok.Getter;
import org.security_study.domain.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

/*
이 클래스는 Resource Sever(Naver, Google, Kakao 등)에서 제공하지 않는 정보들을 우리 서비스에서 갖고 있기 위해 사용
따라서 기본적으로 제공되는 정보만 사용해도 괜찮다면, 구현하지 않고, DefaultOAuth2User 클래스를 사용하면 된다.
 */
@Getter
public class CustomOAuth2User extends DefaultOAuth2User {
    private String email;
    private Long userId;
    private Role role;

    /*
    1. super()를 통해 부모 클래스인 DefaultOAuth2User를 생성
    2. email, role, userId 파라미터를 추가로 받아서 주입 -> CustomOAuth2User 생성
     */
    public CustomOAuth2User(
            Collection<? extends GrantedAuthority> authorities,
            Map<String, Object> attributes,
            String nameAttributeKey,
            String email,
            Long userId,
            Role role
    ) {
        super(authorities, attributes, nameAttributeKey);
        this.email = email;
        this.userId = userId;
        this.role = role;
    }
}
