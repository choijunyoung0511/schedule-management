package com.choijunyoung.schedulemanagement.dto.login;

public class LoginResponse {
    //로그인 응답
    private String username;

    public LoginResponse(String username) {
        this.username = username;
    }
    public String getUsername() {
        return username;
    }
}
