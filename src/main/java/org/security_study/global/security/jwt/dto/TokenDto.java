package org.security_study.global.security.jwt.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenDto {
    //JWT에 대한 인증 타입. 이후 HTTP 헤더에 prefix로 붙이는 타입
    private String grantType;
    private String accessToken;
    private String refreshToken;

    @Builder
    public TokenDto(String grantType, String accessToken, String refreshToken) {
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
