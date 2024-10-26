package org.security_study.security.config;

import lombok.RequiredArgsConstructor;
import org.security_study.security.jwt.filter.JwtAuthorizationFilter;
import org.security_study.security.jwt.filter.JwtExceptionFilter;
import org.security_study.security.jwt.filter.handler.JwtAccessDeniedHandler;
import org.security_study.security.jwt.filter.handler.JwtAuthenticationEntryPoint;
import org.security_study.security.jwt.util.TokenProvider;
import org.security_study.security.oauth2.CustomOAuth2UserService;
import org.security_study.security.oauth2.handler.OAuth2LoginFailureHandler;
import org.security_study.security.oauth2.handler.OAuth2LoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {
    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))   //세션이 있어도 사용 X, 없으면 만들지도 않음
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                /*
                구체적인 경로(작은 범위) 우선 작성, 큰 범위의 경로는 아래로 가도록 작성
                => 당연하지만 시큐리티는 위에서 아래로 코드를 읽기 때문
                 */
                .authorizeHttpRequests(
                        auth -> {
                            auth
                                    .requestMatchers(HttpMethod.OPTIONS, "**").permitAll()  //주로 CORS 사전 요청 처리를 위해 사용 => 따라서 보안 설정 초기에 위치하는 것이 일반적
                                    .requestMatchers(HttpMethod.POST, "/signup").hasAuthority("ROLE_GUEST")
                                    .requestMatchers("/login", "/reissue").permitAll()
                                    .anyRequest().hasAnyAuthority("ROLE_USER", "ROLE_ADMIN");   //위에서 지정한 경로 이외는 이 권한 중 하나라도 갖고 있는 유저만 접근 가능
                            //                            .anyRequest().authenticated();    //위에서 지정한 경로 이외는 jwt 인증이 필요
                        })

                .exceptionHandling(
                        exception -> {
                            exception
                                    .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                                    .accessDeniedHandler(jwtAccessDeniedHandler);
                        })

                .oauth2Login(
                        oauth2 -> {
                            oauth2
                                    .loginPage("/login")
                                    .successHandler(oAuth2LoginSuccessHandler)
                                    .failureHandler(oAuth2LoginFailureHandler)

                                    /*
                                    userInfoEndpoint -> 로그인 과정에서 사용자 정보가 저장된 엔드포인트에 접근할 때의 설정
                                    로그인 시 사용자 인증이 성공하면 사용자 정보를 공급자(naver, kakao, google...)로부터 가져오는데, 이 정보를 공급자의 사용자 정보 엔드포인트에서 가져온다.
                                     */
                                    .userInfoEndpoint(
                                            userInfoEndpoint -> {
                                                userInfoEndpoint.userService(customOAuth2UserService);  //로그인 시 사용자 정보를 가져와 처리하는 서비스 설정
                                            }
                                    );
                        }
                )
                .addFilterBefore(new JwtAuthorizationFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtExceptionFilter(), JwtAuthorizationFilter.class);

                return http.build();
    }

    /*
    이 부분은 CORS 설정을 정의하는 것.
    다른 도메인, 포트, 프로토콜로부터 오는 요청을 허용하거나 제한하기 위해 사용
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(List.of("http://localhost:8080"));   //허용할 Origin 패턴 설정(url)
        configuration.setAllowedHeaders(List.of("*"));  //요청에서 허용할 HTTP 헤더 설정 -> 여기서는 모든 헤더 허용
        configuration.setAllowedMethods(List.of("*"));  //허용할 HTTP 메서드 설정 -> 여기서는 모든 메서드 허용(GET, POST...)
        configuration.setAllowCredentials(true);    //자격 증명(쿠키, 인증 헤더..)을 허용할지 설정 -> 여기서는 자격 증명을 포함한 요청 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); //Url 패턴별로 CORS 적용을 위해 사용하는 클래스
        source.registerCorsConfiguration("/**", configuration); //특정 경로에 CORS 설정 등록 -> 여기서는 모든 경로에 등록
        return source;
    }
}

/*
< 전체 순서 >
    request -> JwtExceptionFilter -> JwtAuthorizationFilter -> (예외 발생 시) JwtAuthenticationEntryPoint or JwtAccessDeniedHandler
    -> 시큐리티 필터들 -> OAuth2LoginSuccessHandler or OAuth2LoginFailureHandler

< 토큰 만료 시 순서 >
    request -> JwtExceptionFilter -> JwtAuthorizationFilter -> JwtExceptionFilter
    필터를 두 번 통과하는 것이 아니라, JwtAuthorizationFilter에서 예외가 발생하면 던지기 때문.

< 토큰과 permitAll() 없이 권한이 필요한 기능 사용 시 순서 >
    request -> JwtExceptionFilter -> JwtAuthorizationFilter -> JwtAuthenticationEntryPoint
 */