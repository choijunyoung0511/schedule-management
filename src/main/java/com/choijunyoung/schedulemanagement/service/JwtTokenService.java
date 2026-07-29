package com.choijunyoung.schedulemanagement.service;

public interface JwtTokenService {
    // 토큰 만드는거, 토큰안의 username꺼내는거, 토큰검증하는거
    String createToken(String username);
    String getUsername(String token);
    boolean validateToken(String token);

}
