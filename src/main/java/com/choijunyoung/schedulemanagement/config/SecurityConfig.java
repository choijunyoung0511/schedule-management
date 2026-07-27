package com.choijunyoung.schedulemanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    //비밀번호 암호화 도구를 스프링에 등록

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    //어떤 API는 로그인 없이 접근 가능한지 보안 규칙 설정
    //밑에 코드는 주소별 접근 권한을 정하는 부분
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/users","/users/login").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}