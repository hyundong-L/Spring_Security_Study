package org.security_study.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class SecurityUtil {

    //시큐리티 컨텍스트에 저장된 사용자 정보 불러오기(현재 로그인 유저 찾는 용도)
    public static Long getCurrentUserId() {
        //메서드 내에서 변경되지 않도록 보장하기 위해 final 사용
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null || authentication.getName().equals("anonymousUser")) {
            throw new RuntimeException("Security Context에 인증 정보가 없습니다.");
        }

        return Long.parseLong(authentication.getName());
    }
}

/*
< if문 설명 >

authentication == null
    Security Context에 인증 정보가 존재하지 않을 때

authentication.getName() == null
    인증 객체가 존재하지만, 사용자 이름이 없는 경우. 즉, 인증 정보 설정이 잘못 되었거나, 인증 프로세스가 올바르게 진행되지 않은 것.

authentication.getName().equals("anonymousUser")
    시큐리티에서 익명 사용자로 간주되는 경우. 즉, 인증되지 않은 사용자가 접근하는 것을 막기 위해 사용
 */