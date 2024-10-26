package org.security_study.security.jwt.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.security_study.domain.Role;
import org.security_study.security.jwt.dto.TokenDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

@Slf4j
@Component
public class TokenProvider {
    private static final String BEARER_TYPE = "Bearer";

    @Value("${jwt.expiration.access}") //access token 만료 시간
    private Long accessTokenExpiration;

    //refresh token 만료 시간
    @Value("${jwt.expiration.refresh}")
    private Long refreshTokenExpiration;

    private final Key key;  //jwt의 토큰 서명을 생성하고 검증하는데 사용

    public TokenProvider(@Value("${jwt.secret}") String secretKey) {    //secret key 생성
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenDto generateToken(Long userId, Role role) {
        String accessToken = generateAccessToken(userId, role);
        String refreshToken = generateRefreshToken();

        return TokenDto.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    //Access Token 만료 시 사용
    public TokenDto generateAccessTokenByRefreshToken(Long userId, Role role, String refreshToken) {
        String accessToken = generateAccessToken(userId, role);

        return TokenDto.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }


    public String generateAccessToken(Long userId, Role role) {
        Date now = new Date();
        Date accessTokenExpireTime = new Date(now.getTime() + accessTokenExpiration);

        //Payload에 사용자를 찾을 수 있게 정보와 권한이 저장되어야 한다.
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("role", role.name())
                .setExpiration(accessTokenExpireTime)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact(); //컴팩트화 -> JWT를 문자열로 반환하는 역할
    }

    //refresh token은 access token과 다르게 재발급을 위한 것이므로 중요 정보(claim) 없이 만료 시간만 담아도 된다.
    public String generateRefreshToken() {
        Date now = new Date();
        Date refreshTokenExpireTime = new Date(now.getTime() + refreshTokenExpiration);

        return Jwts.builder()
                .setExpiration(refreshTokenExpireTime)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact(); //컴팩트화 -> JWT를 문자열로 반환하는 역할
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        if (claims.get("role") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰");
        }

        //권한을 하나씩만 갖는다고 했을 때는 분리하여 담을 필요 없이 이런 식으로 작성하면 된다.
        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(claims.get("role").toString()));

        //Spring Security의 UserDetails 객체를 생성.
        //"" -> 패스워드. 여기서는 사용하지 않기 때문에 공백으로 넣는다. 토큰에는 비밀번호가 저장되어 있지 않기 때문이며, 대신하여 토큰의 서명을 통해 검증
        UserDetails principal = new User(claims.getSubject(), "", authorities); //직접 생성한 User 아님!

        //인증 객체 반환. 현재 로그인 되어있는 사용자의 jwt 토큰을 검사하고, 이를 통해 토큰에서 추출한 정보를 통해 찾은 사용자 정보와 권한이 DB와 일치하는지 확인.
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public boolean validateToken(String token) {
        try {
            //setSigningKey() -> JWT의 서명 확인 시 필요한 key 설정
            //parseClaimsJws() -> JWT 토큰을 분석, 확인
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (UnsupportedJwtException | MalformedJwtException exception) {
            log.info("유효하지 않은 JWT 토큰");
        } catch (ExpiredJwtException exception) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (IllegalArgumentException exception) {
            log.info("JWT 토큰 값이 들어있지 않습니다.");
        } catch (SignatureException exception) {
            log.info("JWT 토큰 서명이 유효하지 않습니다.");
        }
        return false;
    }
}
