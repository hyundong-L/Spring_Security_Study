package org.security_study.security.oauth2.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.security_study.domain.Role;
import org.security_study.security.jwt.dto.TokenDto;
import org.security_study.security.jwt.util.TokenProvider;
import org.security_study.security.oauth2.CustomOAuth2User;
import org.security_study.service.TokenService;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

/*
로그인 성공 후 실행되는 핸들러
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final TokenProvider tokenProvider;
    private final TokenService tokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 로그인 성공");

        try {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

            Long userId = oAuth2User.getUserId();
            Role role = oAuth2User.getRole();

            //토큰 발행
            TokenDto tokenDto = tokenProvider.generateToken(userId, role);
            log.info("Access Token : {}", tokenDto.getAccessToken());
            log.info("Refresh Token : {}", tokenDto.getRefreshToken());

            //Refresh token 저장
            tokenService.updateRefreshToken(userId, tokenDto.getRefreshToken());

            //쿼리 파라미터로 전달 - 리다이렉트 가능
            String frontPath;
            if (oAuth2User.getRole().equals(Role.ROLE_GUEST)) {
                frontPath = "/signup";
                log.info("신규 회원. 회원가입 페이지로 리다이렉트");
            } else {
                frontPath = "/main";
                log.info("기존 회원. 메인 페이지로 리다이렉트");
            }

            String totalRedirectUrl = makeRedirectUrl(tokenDto, frontPath);
            getRedirectStrategy().sendRedirect(request, response, totalRedirectUrl);


            //HTTP Response Body로 전달 - 리다이렉트 불가 - 1
//            OAuth2LoginResponseDto oAuth2LoginResponseDto = new OAuth2LoginResponseDto();
//
//            if (oAuth2User.getRole().equals(Role.ROLE_GUEST)) {
//                oAuth2LoginResponseDto.setIsNewUser(true);
//                oAuth2LoginResponseDto.setMessage("신규 회원. 회원가입 페이지로 이동");
//            } else {
//                oAuth2LoginResponseDto.setIsNewUser(false);
//                oAuth2LoginResponseDto.setMessage("기존 회원. 메인 페이지로 이동");
//            }
//
//            //객체 -> JSON 문자열로 변환
//            Gson gson = new Gson();
//            String jsonDto = gson.toJson(oAuth2LoginResponseDto);
//
//            //응답 설정(JSON 형식, 문자 인코딩)
//            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//            response.setCharacterEncoding("UTF-8");
//
//            //JSON 문자열을 응답 Body에 추가
//            response.getWriter().write(jsonDto);


            //HTTP Response Body로 전달 - 리다이렉트 불가 - 2
//            response.setStatus(HttpServletResponse.SC_OK);
//            response.setContentType("application/json;charset=UTF-8");
//
//            Gson gson = new GsonBuilder().create();
//            String tokenDtoJson = gson.toJson(tokenDto);
//            JsonObject tokenDtoJsonObject = gson.fromJson(tokenDtoJson, JsonObject.class);
//
//            JsonObject finalResponseJsonObject = new JsonObject();
//            finalResponseJsonObject.add("tokenDto", tokenDtoJsonObject);
//
//            if (oAuth2User.getRole().equals(Role.ROLE_GUEST)) {
//                finalResponseJsonObject.addProperty("isNewUser", true);
//                finalResponseJsonObject.addProperty("message", "신규 회원. 회원가입 페이지로 이동");
//            } else {
//                finalResponseJsonObject.addProperty("isNewUser", false);
//                finalResponseJsonObject.addProperty("message", "기존 회원. 메인 페이지로 이동");
//            }
//
//            response.getWriter().write(finalResponseJsonObject.toString());
        } catch (Exception e) {
            throw e;
        }
    }

    //쿼리 파라미터로 전달하는 방법
    private String makeRedirectUrl(TokenDto tokenDto, String frontPath) {
        String redirectUrl = "http://localhost:8080" + frontPath;

        return UriComponentsBuilder.fromUriString(redirectUrl)  //프론트 url로 설정
                .queryParam("grantType", tokenDto.getGrantType())
                .queryParam("accessToken", tokenDto.getAccessToken())
                .queryParam("refreshToken", tokenDto.getRefreshToken())
                .build().toUriString();
    }
}

/*

 */