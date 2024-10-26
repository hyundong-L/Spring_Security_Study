package org.security_study.security.oauth2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.security_study.domain.Role;
import org.security_study.domain.SocialType;
import org.security_study.domain.UserInfo;
import org.security_study.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("CustomOAuth2UserService의 loadUser() 실행 == OAuth2 로그인 요청 진입");

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        /*
        1. DefaultOAuth2UserService의 loadUser()를 통해 소셜 로그인 API의 사용자 정보 제공 URI로 요청을 보내 사용자 정보 얻음
        2. 얻은 정보로 DefaultOAuth2User 객체 생성 후 반환. => 즉, 아래 OAuth2User 객체는 OAuth 서비스에서 가져온 유저 정보를 담고 있는 것
         */
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        //Ex. http://localhost:8080/oauth2/authorization/naver 에서 "naver"가 registrationId
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        SocialType socialType = getSocialType(registrationId);  //registrationId를 통해 SocialType 저장

        //OAuth2 로그인 시 키가 되는 값, 이후 nameAttributeKey로 설정
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        //API가 제공하는 userInfo의 JSON 값 => 유저 정보
        Map<String, Object> attributes = oAuth2User.getAttributes();

        //socialType에 따라서 OAuthAttributes 객체 생성
        OAuthAttributes authAttributes = OAuthAttributes.of(socialType, userNameAttributeName, attributes);
        UserInfo createdUserInfo = getUser(authAttributes, socialType);

        //DefaultOAuth2User를 상속한 CustomOAuth2User 객체 반환
        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(createdUserInfo.getRole().name())),
                attributes,
                authAttributes.getNameAttributeKey(),
                createdUserInfo.getEmail(),
                createdUserInfo.getId(),
                createdUserInfo.getRole()
        );
    }

    private SocialType getSocialType(String registrationId) {
        if (registrationId.equals("naver")) {
            return SocialType.NAVER;
        } else if (registrationId.equals("google")) {
            return SocialType.GOOGLE;
        }
        return SocialType.KAKAO;
    }

    /*
    email을 통해 사용자 검색
    if: 사용자 존재 -> 반환
    else: DB에 저장
     */
    private UserInfo getUser(OAuthAttributes attributes, SocialType socialType) {
        return userRepository.findByEmail(
                attributes.getOAuth2UserInfo().getEmail()
        ).orElseGet(() -> saveUser(attributes, socialType));
    }

    private UserInfo saveUser(OAuthAttributes attributes, SocialType socialType) {
        return userRepository.save(
                UserInfo.builder()
                        .email(attributes.getOAuth2UserInfo().getEmail())
                        .role(Role.ROLE_GUEST)
                        .socialType(socialType)
                        .build()
        );
    }
}
