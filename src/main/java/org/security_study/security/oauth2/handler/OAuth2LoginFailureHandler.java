package org.security_study.security.oauth2.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.info("OAuth2 로그인 실패. 로그인 방식 선택 페이지로 리다이렉트 : {}", exception.getMessage());

        String redirectUrl = UriComponentsBuilder.fromUriString("http://localhost:8080/login").build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
