package com.choijunyoung.schedulemanagement.dto;

import com.choijunyoung.schedulemanagement.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UserCreateRequest {
    //dto vs entity : 엔터티는 DB와 대화하는 객체이다. DTO는 외부와 대화하는 객체임
    //서로 용도가 다르다.
    @NotBlank(message = "아이디는 필수입니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotNull(message = "역할은 필수입니다.")
    private Role role;

    // 각 상자안의 값들을 꺼내는 코드이다.
    // 설계도
    // 회원가입 요청이 오면 스프링은 이 설계도를 보고
    // DTO객체를 하나 생성한다.
    // NotBlank를 쓴 이유 "".null 값 저장되면안되서
    // NotBlank vs NotNull의 차이 Role에는 "" 이라는개념이 없다. 널만 막으면됨

    public String getUsername() {
        return username;
    }


    public String getPassword()
    {
        return password;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

}
