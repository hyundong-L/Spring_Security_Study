package org.security_study.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.security_study.dto.token.TokenDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collections;
import java.util.Date;

@Component
@Slf4j
public class TokenProvider {
    @Value("${jwt.expiration.access}")
    private String accessTokenExpireTime;
    @Value("${jwt.expiration.refresh}")
    private String refreshTokenExpireTime;

    private final Key key;

    //application.yml에서 secret값 가져와서 key에 저장
    public TokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    //로그인 시 access, refresh token 모두 발행
    public TokenDto generateToken(Authentication authentication) {
        //여기서는 무조건 하나의 권한만 사용자에게 부여할 것이기 때문
        String authority = authentication.getAuthorities().stream()
                .findFirst()  //첫 번째 권한 선택
                .map(GrantedAuthority::getAuthority)  //권한 이름을 가져오기
                .orElseThrow(() -> new IllegalStateException("No authority found"));

        String accessToken = generateAccessToken(authentication.getName(), authority);
        String refreshToken = generateRefreshToken();

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    //Access Token 생성
    public String generateAccessToken(String userId, String authority) {
        Date now = new Date();
        Date accessExpiryDate = new Date(now.getTime() + Long.parseLong(accessTokenExpireTime));

        //헤더를 따로 추가하지 않는 이유는 JWT 라이브러리에서 자동으로 추가하기 때문
        return Jwts.builder()
                .setSubject(String.valueOf(userId))  //sub : 토큰 제목(사용자에 대한 식별값)
                .claim("authority", authority)
                .setExpiration(accessExpiryDate)    //exp : 만료 날짜
                .signWith(key, SignatureAlgorithm.HS512)    //서명
                .compact();
    }

    //Refresh Token 생성
    public String generateRefreshToken() {
        Date now = new Date();
        Date refreshExpiryDate = new Date(now.getTime() + Long.parseLong(refreshTokenExpireTime));

        //Refresh token은 Access Token 재발급 용도. 즉 DB에 저장된 내용과 같은지 확인하면 되기 때문에 사용자에 관한 정보는 없어도 된다.
        return Jwts.builder()
                .setExpiration(refreshExpiryDate)   //exp : 만료 날짜
                .signWith(key, SignatureAlgorithm.HS512)    //서명
                .compact();
    }

    //Access Token 만료 시 Refresh Token을 사용해 재발급
    public TokenDto generateAccessTokenByRefreshToken(String userId, String authority, String refreshToken) {
        String accessToken = generateAccessToken(userId, authority);

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    //Access Token에 들어있는 사용자 정보를 꺼내 Authentication 객체를 생성
    public Authentication getAuthentication(String accessToken) {
        //JWT 토큰 복호화
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken)
                .getBody();

        GrantedAuthority authority = new SimpleGrantedAuthority(claims.get("authority").toString());
        UserDetails principal = new User(claims.getSubject(), "", Collections.singletonList(authority));

        return new UsernamePasswordAuthenticationToken(principal, "", Collections.singletonList(authority));
    }

    //토큰 정보를 검증(만료 되었는지, 올바른 형식인지 등등)
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (UnsupportedJwtException | MalformedJwtException e) {   //형식이 잘못되었거나, 지원되지 않는 형식
            log.error("JWT is not supported or has an incorrect format");
        } catch (SignatureException e) {    //서명이 올바르지 않을 때
            log.error("JWT signature validation failed");
        } catch (ExpiredJwtException e) {   //만료
            log.error("JWT is expired");
        } catch (IllegalArgumentException e) {  //토큰이 비어있거나, 공백이 들었을 때
            log.error("JWT is null or empty or only white space");
        } catch (Exception e) { //이외의 예외
            log.error("JWT Exception other than the above cases");
        }

        return false;
    }
}
