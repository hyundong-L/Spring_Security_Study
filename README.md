# OAuth2 사용 X

### 과정
1. FE - 로그인 요청 
2. 우리 서버(Client) - 인증 서버로 로그인 요청
   - Client ID, Redirect URI, Response Type, Scope 등을 포함하여 요청 전송
3. 인증 서버 - FE로 로그인 페이지 제공
4. FE에서 사용자가 로그인
5. 인증 서버는 인가 코드(Authorization Code)를 FE로 발급
6. 인증 코드를 BE로 리다이렉트(Redirect URI 사용)
7. BE에서 인가 코드, Client ID, Client Secret을 사용해 Access Token 요청
   - 자체 발급 토큰과 다름
8. 인증 서버는 Access Token BE로 발급
9. DB에 저장


* * * 


### 참고
- 8번에서 받은 Access Token을 사용해 리소스 서버로부터 API 호출 가능