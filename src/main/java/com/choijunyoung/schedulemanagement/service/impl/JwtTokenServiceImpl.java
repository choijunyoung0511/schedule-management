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
public class JwtTokenServiceImpl implements JwtTokenService {

    private final JwtConfig jwtConfig;

    public JwtTokenServiceImpl(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    @Override
    public String createToken(String username) {
        Date now = new Date();

        Date expirationDate =
                new Date(now.getTime() + jwtConfig.getExpiration());

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(getSigningKey())
                .compact();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes =
                Decoders.BASE64.decode(jwtConfig.getSecret());

        return Keys.hmacShaKeyFor(keyBytes);
    }
}