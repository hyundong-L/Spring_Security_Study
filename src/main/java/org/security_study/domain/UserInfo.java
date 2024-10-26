package org.security_study.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "user_info")
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_info_id")
    private Long id;

    @Column(name = "email", unique = true)
    private String email;

    //회원가입 시 추가로 입력 받을 정보
    private String nickname;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @Column(name = "refresh_token")
    private String refreshToken;

    //회원가입 시에 사용
    @Builder
    public UserInfo(String email, Role role, SocialType socialType) {
        this.email = email;
        this.role = role;
        this.socialType = socialType;
    }

    public void updateProfileInfo(String nickname) {
        this.nickname = nickname;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateRole() {
        this.role = Role.ROLE_USER;
    }
}
