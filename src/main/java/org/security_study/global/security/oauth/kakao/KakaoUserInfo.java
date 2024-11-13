package org.security_study.global.security.oauth.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.security_study.domain.user.entity.SocialType;
import org.security_study.global.security.oauth.abstracts.OAuthUserInfo;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoUserInfo implements OAuthUserInfo {
    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @JsonIgnoreProperties(ignoreUnknown = true)
    record KakaoAccount(String email) {}

    @Override
    public String getEmail() {
        return kakaoAccount.email;
    }

    @Override
    public SocialType getSocialType() {
        return SocialType.KAKAO;
    }
}

/**
 * https://kapi.kakao.com/v2/user/me로 요청을 보냈을 때 받는 값들.
 *
 * 받는 값들은 아래 주소 참고
 * https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#req-user-info
 *
 *
 * @JsonProperty란?
 * 직렬화 즉, 객체를 JSON 형식으로 변환할 때 Key의 이름 설정 가능하게 만드는 어노테이션
 *
 *
 * @JsonIgnoreProperties란?
 * 역직렬화 과정 즉, JSON 데이터를 객체에 매핑하는 과정에 알 수 없는 속성이 JSON 데이터에 있어도 에러를 뱉지 않고 무시
 * 클래스 단위에 붙여서 사용, 기본 값은 false이며, true일 때 적용
 * 위 경우는 우리가 필요한 값만 담기 위해 사용
 */