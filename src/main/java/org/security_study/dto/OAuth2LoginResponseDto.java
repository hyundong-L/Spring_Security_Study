package org.security_study.dto;

import lombok.Setter;
import org.security_study.security.jwt.dto.TokenDto;

@Setter
public class OAuth2LoginResponseDto {
    private TokenDto token;
    private Boolean isNewUser;
    private String message;
}
