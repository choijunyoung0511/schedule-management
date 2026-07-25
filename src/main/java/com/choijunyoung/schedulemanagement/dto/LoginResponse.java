package com.choijunyoung.schedulemanagement.dto;

public class LoginResponse {
    private String username;

    public LoginResponse(String username) {
        this.username = username;
    }
    public String getUsername() {
        return username;
    }
}
