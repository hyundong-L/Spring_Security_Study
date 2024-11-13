package org.security_study.domain.user.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_info_id")
    private Long id;

    private String email;

    //회원가입 시 추가로 입력 받을 정보
    private String nickname;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_type")
    private SocialType socialType;

    @Column(name = "refresh_token")
    private String refreshToken;

    //회원가입 시 사용
    @Builder
    public UserInfo(String email, SocialType socialType) {
        this.email = email;
        this.role = Role.ROLE_GUEST;
        this.socialType = socialType;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateAdditionalInfo(String nickname) {
        this.nickname = nickname;
        this.role = Role.ROLE_USER;
    }
}
