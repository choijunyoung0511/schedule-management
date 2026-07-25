package com.choijunyoung.schedulemanagement.dto.login;

public class LoginResponse {
    private String username;

    public LoginResponse(String username) {
        this.username = username;
    }
    public String getUsername() {
        return username;
    }
}
