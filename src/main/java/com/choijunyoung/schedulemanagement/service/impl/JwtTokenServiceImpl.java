package com.choijunyoung.schedulemanagement.service.impl;

import com.choijunyoung.schedulemanagement.config.JwtConfig;
import com.choijunyoung.schedulemanagement.service.JwtTokenService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
//스프링이 객체를 생성해서 관리
public class JwtTokenServiceImpl implements JwtTokenService {

    private final JwtConfig jwtConfig;
    //jwt설정을  저장할 변수

    public JwtTokenServiceImpl(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        //전달받은 JwtConfig를 필드에 저장
    }

    @Override
    public String createToken(String username) {
        //jwt생성 매서드

        Date now = new Date();
        //현재시간
        Date expirationDate =
                new Date(now.getTime() + jwtConfig.getExpiration());
        //현재시간 + 만료시간
        // 토큰 만료 시각 생성
        return Jwts.builder()
                //jwt생성 시작
                .subject(username)
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(getSigningKey())
                .compact();
    }

    private SecretKey getSigningKey() {
        //jwt서명용 비밀키 생성 대서드
        byte[] keyBytes =
                Decoders.BASE64.decode(jwtConfig.getSecret());

        return Keys.hmacShaKeyFor(keyBytes);
        //byte[]를 SecretKey객체로 변환
    }
    @Override
    public String getUsername(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getBody()
                .getSubject();
    }
    @Override
    public boolean validateToken(String token) {
        //유효성 검사
        try{
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);

            return true;

        }catch (Exception e){
            return false;
        }
    }
}