프론트 적용 방법

1. 압축을 풉니다.
2. index.html, style.css, app.js 세 파일을
   src/main/resources/static/
   폴더에 넣습니다.
3. Spring Boot 서버를 실행합니다.
4. 브라우저에서 http://localhost:8080 접속
5. 화면 하단 회원가입 폼에서 테스트합니다.

현재 연결된 API:
POST /users

전송 JSON:
{
  "username": "...",
  "password": "...",
  "name": "...",
  "role": "USER"
}

같은 Spring Boot 서버의 static 폴더에서 화면을 띄우므로
fetch("/users") 방식으로 호출하며 별도의 CORS 설정이 필요 없습니다.
