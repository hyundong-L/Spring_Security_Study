package org.security_study.global.security.oauth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 인증 서버로 엑세스 토큰을 요청할 때 사용 - http 요청을 보내야 하므로 빈으로 등록
 *
 * RestTemplate이란?
 * Spring이 지원하는 객체, 간편하게 REST API를 호출 가능하게 만드는 클래스
 * HTTP 프로토콜 메서드(GET, POST, ...)에 적합한 여러 메서드 제공
 */

@Configuration
public class ClientConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}