package org.security_study.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.security_study.global.security.jwt.dto.TokenDto;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResultDto {
    private Boolean isNewUser;
    private TokenDto token;
}
