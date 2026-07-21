package com.choijunyoung.schedulemanagement.dto;

import com.choijunyoung.schedulemanagement.entity.Role;

public class UserCreateRequest {
    //dto vs entity : 엔터티는 DB와 대화하는 객체이다. DTO는 외부와 대화하는 객체임
    //서로 용도가 다르다.
    private String username;
    private String password;
    private String name;
    private Role role;

    // 각 상자안의 값들을 꺼내는 코드이다.
    // 설계도
    // 회원가입 요청이 오면 스프링은 이 설계도를 보고
    // DTO객체를 하나 생성한다.
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public String getName() {
        return name;
    }
    public Role getRole() {
        return role;
    }

}
