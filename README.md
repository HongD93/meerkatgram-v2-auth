# Meerkatgram v2 Auth

Meerkatgram v2의 회원가입·로그인과 프로필 이미지 업로드를 담당하는 Spring Boot 인증 서비스입니다. [API Gateway](https://github.com/HongD93/meerkatgram-v2-scg)가 요청을 받아 JWT를 확인하고, 이 서비스는 회원·토큰을 관리하며 [Post 서비스](https://github.com/HongD93/meerkatgram-v2-post)는 게시글을 관리합니다.

## 주요 기능

- 이메일·비밀번호 회원가입과 로그인, BCrypt 비밀번호 저장
- JWT 접근 토큰 발급, 리프레시 토큰 쿠키를 이용한 재발급, 로그아웃
- 카카오 OAuth2 로그인과 회원 정보 연동
- MinIO 프로필 이미지 업로드와 OpenAPI 문서 제공

## 로그인 흐름

1. `POST /api/auth/login`으로 이메일과 비밀번호를 전달합니다.
2. 저장된 회원과 BCrypt 비밀번호를 확인한 뒤 접근 토큰을 응답하고 리프레시 토큰을 쿠키로 설정합니다.
3. 클라이언트는 접근 토큰을 게이트웨이에 전달합니다. 게이트웨이는 JWT에서 사용자 ID·역할을 추출해 하위 서비스에 전달합니다.
4. `POST /api/auth/reissue-token`은 쿠키의 리프레시 토큰을 DB에 저장된 값과 대조한 뒤 토큰을 재발급합니다. 로그아웃은 저장된 리프레시 토큰과 쿠키를 제거합니다.

인증이 필요한 API는 게이트웨이가 전달하는 `X-User-Id`, `X-User-Role` 헤더로 사용자 정보를 구성합니다. 회원 인증과 게시글 접근을 함께 사용하려면 게이트웨이와 각 서비스를 연결해야 합니다.

## 기술 구성

| 항목 | 구성 |
| --- | --- |
| Java | JDK 21 toolchain |
| 빌드 | Gradle Wrapper 9.5.1 |
| 애플리케이션 | Spring Boot 4.1.0, Spring Security, OAuth2 Client |
| 데이터 접근 | Spring Data JPA, QueryDSL 5.1.0, MySQL |
| 인증 | JJWT 0.12.6 |
| 파일 저장 | MinIO SDK 8.6.0 |
| API 문서 | Springdoc OpenAPI 3.0.3 |

## 시작하기

### 준비

JDK 21, 연결 가능한 MySQL 데이터베이스, MinIO 서버·버킷, 카카오 OAuth 앱 설정이 필요합니다. 실행할 셸이나 IDE의 환경변수에 다음 값을 설정합니다. 기본 설정에 대체값이 없으므로 각 변수를 지정해야 합니다.

| 환경변수 | 용도 |
| --- | --- |
| `APP_PORT` | 인증 서비스 수신 포트 |
| `DB_HOST`, `DB_PORT`, `DB_NAME` | MySQL 접속 대상 |
| `DB_USER`, `DB_PASSWORD` | MySQL 접속 자격 증명 |
| `JWT_SECRET` | JWT 서명에 사용하는 Base64 HMAC 키 |
| `KAKAO_CLIENT_ID`, `KAKAO_CLIENT_SECRET` | 카카오 OAuth 앱 자격 증명 |
| `GATEWAY_URI` | 게이트웨이 주소와 OAuth 콜백 주소 구성 |
| `FRONTEND_CALLBACK_URI` | OAuth 처리 후 프론트엔드 이동 주소 |
| `APP_DESCRIPTION` | OpenAPI 서버 설명 |
| `MINIO_ENDPOINT`, `MINIO_BUCKET` | MinIO 접속 주소와 버킷 |
| `MINIO_ACCESS_KEY`, `MINIO_SECRET_KEY` | MinIO 접속 자격 증명 |
| `MINIO_PROFILE_PATH` | 버킷 내부 프로필 이미지 경로 |

`JWT_SECRET`은 최소 32바이트의 무작위 키를 Base64로 인코딩한 값으로 준비하고, 게이트웨이에도 같은 값을 설정합니다. 카카오에 등록한 리다이렉트 URI는 게이트웨이를 통해 접근하는 `/api/auth/oauth2/callback/kakao`와 일치해야 합니다.

설정은 [application.yaml](src/main/resources/application.yaml)에서 확인할 수 있습니다. 기본 실행은 DB 테이블 구조를 갱신하고 샘플 데이터를 적용할 수 있으므로 개발용 DB를 사용합니다. [prod 프로필](src/main/resources/application-prod.yaml)로 실행하려면 DB 스키마를 미리 준비해야 합니다.

### 실행

저장소 루트에서 실행합니다. 환경변수는 명령을 실행하는 프로세스에 전달해야 하며, `.env` 파일 자동 읽기는 구성되어 있지 않습니다.

```powershell
.\gradlew.bat bootRun
```

macOS/Linux에서는 `bash ./gradlew bootRun`을 사용합니다. `prod` 설정을 선택하려면 다음과 같이 실행합니다.

```powershell
.\gradlew.bat bootRun --args='--spring.profiles.active=prod'
```

### 빌드·테스트

```powershell
.\gradlew.bat build
.\gradlew.bat test
```

테스트에는 Spring Boot 컨텍스트를 시작하는 테스트가 포함되어 있어 실행 설정과 외부 서비스가 필요할 수 있습니다.

## 주요 API

| 경로 | 역할 |
| --- | --- |
| `POST /api/auth/registration` | 회원가입 |
| `POST /api/auth/login` | 로그인 |
| `POST /api/auth/logout` | 로그아웃 |
| `POST /api/auth/reissue-token` | 토큰 재발급 |
| `POST /api/auth/files/profiles` | 프로필 이미지 업로드 |
| `/api/auth/oauth2/authorization/kakao` | 카카오 로그인 시작 |
| `/api-docs` | OpenAPI JSON |

서비스 자체의 Swagger UI는 비활성화되어 있습니다. 문서 UI는 게이트웨이 설정을 함께 확인합니다.

## 구조

| 경로 | 내용 |
| --- | --- |
| `src/main/java/com/meerkatgramv2auth/domain/auth/` | 회원 인증·카카오 로그인 |
| `src/main/java/com/meerkatgramv2auth/domain/file/` | 프로필 파일 업로드 |
| `src/main/java/com/meerkatgramv2auth/global/` | JWT, Security, MinIO, 공통 응답·오류 처리 |
| `src/main/resources/dummy/` | 회원 샘플 SQL |
| `meerkatgram-v2-doc-student/` | MSA·권한·파일 저장·소셜 로그인 학습 자료 |

## 학습 자료

| 문서 | 알 수 있는 내용 |
| --- | --- |
| [학습 안내](meerkatgram-v2-doc-student/00-orientation.md) | v1에서 v2로 바뀌는 구성과 학습 순서 |
| [MSA 구조](meerkatgram-v2-doc-student/01-msa-architecture.md) | 서비스 경계, 게이트웨이 라우팅, 인증·게시글 서비스 분리 |
| [카카오 소셜 로그인](meerkatgram-v2-doc-student/04-social-login-kakao.md) | OAuth2 흐름, 카카오 앱 설정, 로그인 처리와 프론트엔드 연동 |
