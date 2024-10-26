package org.security_study.security.oauth2.userInfo;


import java.util.Map;

/*
아래 반환 예시에 나온 것처럼 네이버는 유저 정보가 "response"로 한 번 더 감싸져있다.
따라서 get으로 response를 가져온 후 다시 get으로 원하는 값을 가져와야 한다.
 */

public class NaverOAuth2UserInfo extends OAuth2UserInfo {
    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        super((Map<String, Object>) attributes.get("response"));
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }
}

/*
< 네이버 유저 정보 Response JSON 예시 >

{
  "resultcode": "00",
  "message": "success",
  "response": {
    "email": "openapi@naver.com",
    "nickname": "OpenAPI",
    "profile_image": "https://ssl.pstatic.net/static/pwe/address/nodata_33x33.gif",
    "age": "40-49",
    "gender": "F",
    "id": "32742776",
    "name": "오픈 API",
    "birthday": "10-01"
  }
}
 */