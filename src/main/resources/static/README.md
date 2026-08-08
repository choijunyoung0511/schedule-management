# Calorie AI Frontend

## 폴더 구조

- index.html
- css/base.css
- css/layout.css
- css/auth.css
- css/meal.css
- css/responsive.css
- js/config.js
- js/state.js
- js/dom.js
- js/api.js
- js/ui.js
- js/auth.js
- js/meal.js
- js/events.js
- js/app.js

## 적용 방법

Spring Boot 프로젝트의 정적 파일 경로에 이 폴더 내용을 복사하세요.

예:
src/main/resources/static/

## 백엔드에 필요한 API

- POST /users
- POST /users/login
- GET /users/me
- POST /files/images
- POST /gemini/analyze-image
  - multipart/form-data
  - file
  - amount
- POST /meals
  - mealType
  - imageUrl
  - amount
- PATCH /meals/{mealId}/analysis
- GET /meals
- GET /meals/today
- DELETE /meals/{mealId}

## 중요

브라우저에서 index.html을 파일로 직접 열지 말고,
Spring Boot 서버 또는 로컬 웹 서버를 통해 실행하세요.
ES Module을 사용하므로 file:// 방식에서는 동작하지 않을 수 있습니다.
