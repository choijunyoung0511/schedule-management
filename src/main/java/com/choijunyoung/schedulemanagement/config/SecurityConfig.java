package com.choijunyoung.schedulemanagement.config;

import com.choijunyoung.schedulemanagement.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    // 매 요청마다 JWT를 확인하는 사용자 정의 필터
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // 생성자 주입
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 회원 비밀번호를 BCrypt 방식으로 암호화하고 비교할 때 사용
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                // JWT 기반 REST API에서는 보통 세션 쿠키 인증을 사용하지 않으므로 CSRF 비활성화
                .csrf(csrf -> csrf.disable())

                // 로그인 상태를 서버 세션에 저장하지 않음
                // 각 요청마다 JWT를 전달받아 인증함
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 요청 경로별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth

                        // 메인 페이지와 정적 파일은 로그인 없이 접근 가능
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/style.css",
                                "/app.js",
                                "/favicon.ico"
                        ).permitAll()

                        // 회원가입과 로그인 API는 JWT 없이 접근 가능
                        .requestMatchers(
                                "/users",
                                "/users/login"
                        ).permitAll()

                        // 위에서 허용하지 않은 모든 요청은 JWT 인증 필요
                        .anyRequest().authenticated()
                )

                // Spring Security가 제공하는 기본 로그인 HTML 화면 사용 안 함
                .formLogin(form -> form.disable())

                // 브라우저 기본 인증 팝업 방식 사용 안 함
                .httpBasic(basic -> basic.disable())

                // Spring Security 기본 인증 필터보다 먼저 JWT 필터 실행
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}